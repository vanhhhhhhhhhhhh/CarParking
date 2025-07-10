const Parking = require('../model/Parking')
const User = require('../model/User')


const parkingController = {

    createParking: async (req, res) => {
        try {
            const imageUrl = req.file ? req.file.path : '';

            const {
                name,
                address,
                coordinates, // chuỗi JSON dạng: "[106.700988, 10.776889]"
                totalSlots,
                availableSlots,
                pricePerHour,
                openTime,
                closeTime
            } = req.body;

            if (
                !name || !address || !coordinates ||
                !totalSlots || !availableSlots ||
                !pricePerHour || !openTime || !closeTime || !imageUrl
            ) {
                return res.status(400).json({ message: 'Vui lòng nhập đầy đủ các trường' });
            }

            let parsedCoordinates;
            try {
                parsedCoordinates = JSON.parse(coordinates);
                if (!Array.isArray(parsedCoordinates) || parsedCoordinates.length !== 2) {
                    throw new Error();
                }
                parsedCoordinates = parsedCoordinates.map(Number);
            } catch (err) {
                return res.status(400).json({
                    message: 'Trường coordinates phải là mảng số [longitude, latitude], ví dụ: [106.7, 10.7]'
                });
            }

            const existing = await Parking.findOne({ name, address });
            if (existing) {
                return res.status(400).json({ message: 'Bãi đỗ xe đã có mặt trong hệ thống' });
            }

            if (totalSlots <= 0) {
                return res.status(400).json({ message: 'Tổng số slot phải lớn hơn 0' });
            }

            if (availableSlots < 0 || availableSlots > totalSlots) {
                return res.status(400).json({ message: 'Số slot khả dụng không hợp lệ' });
            }

            if (pricePerHour < 0) {
                return res.status(400).json({ message: 'Giá theo giờ không được âm' });
            }

            // Tạo bãi đỗ mới
            const newParking = await Parking.create({
                name,
                address,
                location: {
                    coordinates: parsedCoordinates
                },
                totalSlots: Number(totalSlots),
                availableSlots: Number(availableSlots),
                pricePerHour: Number(pricePerHour),
                openTime,
                closeTime,
                imageUrl,
                ownerId: req.userId, 
            });

            return res.status(201).json({
                message: 'Gửi đăng ký bãi đỗ xe thành công',
                data: newParking
            });

        } catch (error) {
            return res.status(500).json(error.message);
        }
    },
    listParking: async (req, res) => {
        try {
            const { 
                latitude,
                longitude,
                distance = 500,
                name
            } = req.query

            console.log('Query parameters:', req.query);

            const query = {}

            if (name) {
                query.name = { $regex: name, $options: 'i' } 
            }

            if (latitude && longitude) {
                query.location = {
                    $near: {
                        $geometry: {
                            type: 'Point',
                            coordinates: [parseFloat(longitude), parseFloat(latitude)]
                        },
                        $maxDistance: parseInt(distance) 
                    }
                }
            }

            const parkings = await Parking.find(query)
            return res.status(200).json({data: parkings})
        } catch (error) {
            console.error('Error in listParking:', error);
            return res.status(500).json(error.message)
        }
    },
    manageRequest: async (req, res) => {
        try {
            const pid = req.params.id
            const {status} = req.body

            if(!['approved', 'rejected'].includes(status)){
                return res.status(400).json({message: 'Trạng thái không hợp lệ'})
            }

            const parking = await Parking.findById(pid)
            if(!parking){
                return res.status(404).json({message: 'Yêu cầu không tồn tại'})
            }

            parking.status = status
            await parking.save()

            if(status === 'approved'){
                await User.findByIdAndUpdate(parking.ownerId, {role: 'owner'})
            }

            return res.status(200).json({message: `Yêu cầu đã ${status === 'approved' ? 'được duyệt' : 'bị từ chối'}`, updatedStatus: parking.status})
        } catch (error) {
            return res.status(500).json(error.message)
        }
    },
    getParkingById: async (req, res) => {
        try {
            const pid = req.params.id
            const parking = await Parking.findById(pid)
            if (!parking) {
                return res.status(404).json({ message: 'Bãi đỗ xe không tồn tại' });
            }
            return res.status(200).json({ data: parking });
        } catch (error) {
            return res.status(500).json(error.message);
        }
    }
}

module.exports = parkingController