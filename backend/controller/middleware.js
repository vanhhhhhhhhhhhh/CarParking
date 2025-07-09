const User = require('../model/User')
const jwt = require('jsonwebtoken')
require('dotenv').config()


const middlewareController = {

    verifyToken: async (req, res, next) => {
        const authHeader = req.header('Authorization')
        const token = authHeader && authHeader.split(' ')[1]

        if (!token) {
            return res.status(401).json({ success: false, message: 'Access token không tìm thấy' })
        }
        try {
            const decoded = jwt.verify(token, process.env.ACCESS_TOKEN_SECRET)
            req.userId = decoded.userId

            const user = await User.findById(req.userId)
            if (!user) {
                return res.status(404).json({ success: false, message: 'Không tìm thấy người dùng' })
            }
            req.user = user
            next()
        } catch (error) {
            return res.status(403).json({ success: false, message: 'Token không hiệu lực' })
        }
    },

    verifyOwner: (req, res, next) => {
        if(!req.user){
            return res.status(401).json({success: false, message: 'Không được phép. Vui lòng đăng nhập'})
        }
        if(req.user.role !== 'owner'){
            return res.status(403).json({success: false, message: 'Yêu cầu bị từ chối. Chỉ có chủ bãi mới có quyền'})
        }
        next()
    },

    verifyAdmin: (req, res, next) => {
        if(!req.user){
            return res.status(401).json({success: false, message: 'Không được phép. Vui lòng đăng nhập'})
        }
        if(req.user.role !== 'admin'){
            return res.status(403).json({success: false, message: 'Yêu cầu bị từ chối. Chỉ có admin mới có quyền'})
        }
        next()
    }

}

module.exports = middlewareController