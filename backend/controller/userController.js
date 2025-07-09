const User = require('../model/User')
const argon2 = require('argon2')
const jwt = require('jsonwebtoken')
require('dotenv').config()

const userController = {


    register: async (req, res) => {
        try {
            const { fullName, phone, password, confirmPassword } = req.body
            if (!fullName || !phone || !password || !confirmPassword) {
                return res.status(400).json({ message: 'Vui lòng nhập đẩy đủ các trường' })
            }

            const phoneRegex = /^\d{10}$/
            if (!phoneRegex.test(phone)) {
                return res.status(400).json({ message: 'Số điện thoại không hợp lệ. Vui lòng nhập 10 chữ số' });
            }

            const user = await User.findOne({ phone: phone })
            if (user) {
                return res.status(400).json({ message: 'Số điện thoại đã tồn tại' })
            }

            if (password.length < 6) {
                return res.status(400).json({ message: 'Mật khẩu phải chứa ít nhất 6 ký tự' })
            }

            if (confirmPassword !== password) {
                return res.status(400).json({ message: 'Mật khẩu không khớp! Vui lòng nhập lại' })
            }

            const encryption = await argon2.hash(password)

            const newUser = new User({
                phone,
                password: encryption,
                fullName
            })

            await newUser.save()
            return res.status(201).json({ message: 'Đăng ký tài khoản thành công', data: newUser })
        } catch (error) {
            return res.status(500).json(error.message)
        }
    },


    login: async(req, res) => {
        try {
            const {phone, password} = req.body
            if(!phone || !password){
                return res.status(400).json({success: false, message: 'Vui lòng nhập đầy đủ các trường'})
            }

            const user = await User.findOne({phone: phone})
            if(!user){
                return res.status(404).json({success: false, message: 'Số điện thoại hoặc mật khẩu không đúng'})
            }

            const validPassword = await argon2.verify(user.password, password)
            if(!validPassword){
                return res.status(400).json({success: false, message: 'Số điện thoại hoặc mật khẩu không đúng'})
            }

            const accessToken = jwt.sign({userId: user._id, role: user.role}, process.env.ACCESS_TOKEN_SECRET)

            return res.status(200).json({success: true, message: 'Đăng nhập thành công', user, accessToken})
        } catch (error) {
            return res.status(500).json(error.message)
        }
    },
}


module.exports = userController