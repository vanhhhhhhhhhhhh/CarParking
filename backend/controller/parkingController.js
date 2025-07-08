const Parking = require('../model/Parking')


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
            return res.status(500).json({ message: error.message });
        }
    }


}

module.exports = parkingController