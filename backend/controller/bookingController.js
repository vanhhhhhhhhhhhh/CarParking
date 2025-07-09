const Booking = require('../model/Booking')
const LicensePlate = require('../model/LicensePlate')
const Parking = require('../model/Parking')

const isValidUnixMillisecond = (unixMillisecond) => {
    return typeof unixMillisecond === 'number' && unixMillisecond >= 0
}

const checkOverLap = async (startTimeDate, endTimeDate, parkingId) => {
    const bookingCount = await Booking.where({
        parkingId,
        startTime: { $lte: endTimeDate },
        endTime: { $gte: startTimeDate },
        status: {
            $nin: ['cancelled', 'completed']
        }
    }).countDocuments();

    return bookingCount > 0
}

const ONE_DAY_IN_MILLISECONDS = 24 * 60 * 60 * 1000
const ONE_HOUR_IN_MILLISECONDS = 3600 * 1000
const TWO_DAYS_IN_MILLISECONDS = 48 * ONE_HOUR_IN_MILLISECONDS
const ROUND_FACTOR = 1000

const calculateTotalPrice = (pricePerHour, startTime, endTime) => {
    const durationInHours = (endTime - startTime) / 3600000
    return Math.floor((pricePerHour * durationInHours) / ROUND_FACTOR) * ROUND_FACTOR
}

function formatDate(date) {
    const pad2 = n => n.toString().padStart(2, '0');
  
    const hours   = pad2(date.getHours());
    const minutes = pad2(date.getMinutes());
    const day     = pad2(date.getDate());
    const month   = pad2(date.getMonth() + 1);
    const year    = date.getFullYear();
  
    return `${hours}:${minutes} - ${day}/${month}/${year}`;
  }
  

const bookingController = {
    createBooking: async (req, res) => {
        try {
            const {
                parkingId,
                licensePlateId,
                startTime,
                endTime
            } = req.body;

            if (!parkingId || !licensePlateId || !startTime || !endTime) {
                return res.status(400).json({ message: 'Vui lòng nhập đầy đủ các trường' });
            }

            const parking = await Parking.findById(parkingId)
            if (!parking) {
                return res.status(400).json({ message: 'Bãi đỗ không tồn tại' });
            }

            const licensePlateData = await LicensePlate.findById(licensePlateId)
            if (!licensePlateData) {
                return res.status(400).json({ message: 'Biển số xe không tồn tại' });
            }

            if (!isValidUnixMillisecond(startTime) || !isValidUnixMillisecond(endTime)) {
                return res.status(400).json({ message: 'Thời gian không hợp lệ' });
            }

            if (startTime >= endTime) {
                return res.status(400).json({ message: 'Thời gian bắt đầu phải trước thời gian kết thúc' });
            }

            const durationInMilliseconds = endTime - startTime

            if (durationInMilliseconds < ONE_HOUR_IN_MILLISECONDS) {
                return res.status(400).json({ message: 'Thời gian đặt chỗ phải lớn hơn hoặc bằng 1 giờ' });
            }

            if (durationInMilliseconds > TWO_DAYS_IN_MILLISECONDS) {
                return res.status(400).json({ message: 'Thời gian đặt chỗ phải nhỏ hơn hoặc bằng 2 ngày' });
            }

            const startTimeDate = new Date(startTime)
            const endTimeDate = new Date(endTime)

            if (parking.availableSlots <= 0) {
                return res.status(400).json({ message: 'Bãi đỗ đã đầy' });
            }

            if (await checkOverLap(startTimeDate, endTimeDate, parkingId)) {
                return res.status(400).json({ message: 'Thời gian đặt chỗ đã bị trùng lặp' });
            }

            const newBooking = await Booking.create({
                userId: req.userId,
                parkingId,
                status: 'pending',
                vehicleNumber: licensePlateData.licensePlate,
                startTime,
                endTime,
                totalPrice: calculateTotalPrice(parking.pricePerHour, startTime, endTime)
            });

            await Parking.findByIdAndUpdate(parkingId, {
                $inc: { availableSlots: -1 }
            })

            return res.status(201).json({
                message: 'Đặt chỗ thành công',
                data: newBooking
            });

        } catch (error) {
            return res.status(500).json({ message: 'Lỗi server' });
        }
    },

    calculatePrice: async (req, res) => {
        try {
            const { parkingId, startTime, endTime } = req.body

            if (!parkingId || !startTime || !endTime) {
                return res.status(400).json({ message: 'Vui lòng nhập đầy đủ các trường' });
            }

            const parking = await Parking.findById(parkingId)
            if (!parking) {
                return res.status(400).json({ message: 'Bãi đỗ không tồn tại' });
            }

            const durationInMilliseconds = endTime - startTime

            if (durationInMilliseconds < ONE_HOUR_IN_MILLISECONDS) {
                return res.status(400).json({ message: 'Thời gian đặt chỗ phải lớn hơn hoặc bằng 1 giờ' });
            }

            if (durationInMilliseconds > TWO_DAYS_IN_MILLISECONDS) {
                return res.status(400).json({ message: 'Thời gian đặt chỗ phải nhỏ hơn hoặc bằng 2 ngày' });
            }

            const totalPrice = calculateTotalPrice(parking.pricePerHour, startTime, endTime)

            return res.status(200).json({ data: totalPrice })
        } catch (error) {
            return res.status(500).json({ message: 'Lỗi server' })
        }
    },

    listBookings: async (req, res) => {
        try {
            const query = { userId: req.userId }

            if (!isValidUnixMillisecond(req.query.startDate) || !isValidUnixMillisecond(req.query.endDate)) {
                return res.status(400).json({ message: 'Thời gian không hợp lệ' });
            }

            if (req.query.startDate && req.query.endDate) {
                query.startTime = { $gte: new Date(req.query.startDate) }
                query.endTime = { $lte: new Date(req.query.endDate) + ONE_DAY_IN_MILLISECONDS }
            }

            const bookings = await Booking.find(query).populate('parkingId')
            const mappedBookings = bookings.map(b => ({
                id: b._id,
                totalPrice: b.totalPrice,
                parkingName: b.parkingId.name,
                address: b.parkingId.address,
                status: b.status,
                startTime: formatDate(b.startTime),
                endTime: formatDate(b.endTime),
            }));
            return res.status(200).json({ data: mappedBookings })
        } catch (error) {
            return res.status(500).json({ message: 'Lỗi server' })
        }
    },

    cancelBooking: async (req, res) => {
        try {
            const { id } = req.params
            const booking = await Booking.findById(id)
            if (!booking) {
                return res.status(400).json({ message: 'Đặt chỗ không tồn tại' });
            }

            if (booking.status !== 'pending') {
                return res.status(400).json({ message: 'Không thể hủy đặt chỗ đã được xác nhận' });
            }

            booking.status = 'cancelled'
            await booking.save()

            await Parking.findByIdAndUpdate(booking.parkingId, {
                $inc: { availableSlots: 1 }
            })

            return res.status(200).json({ message: 'Hủy đặt chỗ thành công' })
        } catch (error) {
            return res.status(500).json({ message: 'Lỗi server' })
        }
    }
}

module.exports = bookingController