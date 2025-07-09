const router = require('express').Router()
const bookingController = require('../controller/bookingController')
const middleware = require('../controller/middleware')

router.post('/create', middleware.verifyToken, bookingController.createBooking)

router.get('/list', middleware.verifyToken, bookingController.listBookings)

router.post('/calculate-price', middleware.verifyToken, bookingController.calculatePrice)

router.put('/cancel/:id', middleware.verifyToken, bookingController.cancelBooking)

module.exports = router