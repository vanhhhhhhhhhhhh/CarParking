const router = require('express').Router()
const licensePlateController = require('../controller/licensePlateController')
const middleware = require('../controller/middleware')

router.post('/create', middleware.verifyToken, licensePlateController.createLicensePlate)

router.get('/list', middleware.verifyToken, licensePlateController.listLicensePlate)

module.exports = router