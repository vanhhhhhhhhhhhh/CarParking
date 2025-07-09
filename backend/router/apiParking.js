const router = require('express').Router()
const parkingController = require('../controller/parkingController')
const middleware = require('../controller/middleware')
const imageUpload = require('../controller/imageUpload')
const uploadCloud = require('../controller/imageUpload')

router.post('/create', middleware.verifyToken, uploadCloud.single('image'), parkingController.createParking)

router.get('/list', parkingController.listParking)

router.put('/request/:id', middleware.verifyToken, middleware.verifyAdmin, parkingController.manageRequest)

router.get('/:id', parkingController.getParkingById)

module.exports = router