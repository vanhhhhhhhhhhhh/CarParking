const cloudinary = require('cloudinary').v2
const {CloudinaryStorage} = require('multer-storage-cloudinary')
const multer = require('multer')
require('dotenv').config()


cloudinary.config({
    cloud_name: process.env.CLOUDINARY_NAME,
    api_key: process.env.CLOUDINARY_API,
    api_secret: process.env.CLOUDINARY_SECRET
})

const storage = new CloudinaryStorage({
    cloudinary,
    allowedFormats: ['jpg', 'jpeg', 'png', 'webp'],
    params: {
        folder: 'CarParking'
    }
})

const uploadCloud = multer({storage})

module.exports = uploadCloud