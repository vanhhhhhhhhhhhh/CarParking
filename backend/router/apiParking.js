const router = require('express').Router()
const parkingController = require('../controller/parkingController')
const middleware = require('../controller/middleware')
const imageUpload = require('../controller/imageUpload')
const uploadCloud = require('../controller/imageUpload')

router.post('/create', middleware.verifyToken, uploadCloud.single('image'), parkingController.createParking)

module.exports = router