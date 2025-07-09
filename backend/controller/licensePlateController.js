const LicensePlate = require('../model/LicensePlate')

const licensePlateController = {
    createLicensePlate: async (req, res) => {
        try {
            let {
                licensePlate
            } = req.body;

            if (
                !licensePlate
            ) {
                return res.status(400).json({ message: 'Vui lòng nhập đầy đủ các trường' });
            }

            licensePlate = licensePlate.trim().toUpperCase();

            const existing = await LicensePlate.findOne({ licensePlate, userId: req.userId });
            if (existing) {
                return res.status(400).json({ message: 'Biển số xe đã có mặt trong hệ thống' });
            }

            const newLicensePlate = await LicensePlate.create({
                licensePlate,
                userId: req.userId, 
            });

            return res.status(201).json({
                message: 'Đăng ký biển số xe thành công',
                data: newLicensePlate
            });

        } catch (error) {
            console.error('Error creating license plate:', error);
            return res.status(500).json({ message: 'Lỗi server' });
        }
    },

    listLicensePlate: async (req, res) => {
        try {
            const licensePlates = await LicensePlate.find({userId: req.userId})
            return res.status(200).json({data: licensePlates})
        } catch (error) {
            console.error('Error listing license plates:', error);
            return res.status(500).json({ message: 'Lỗi server' })
        }
    },
}

module.exports = licensePlateController