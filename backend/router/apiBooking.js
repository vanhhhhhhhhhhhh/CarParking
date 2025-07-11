const router = require('express').Router()
const bookingController = require('../controller/bookingController')
const middleware = require('../controller/middleware')

router.post('/create', middleware.verifyToken, bookingController.createBooking)

router.get('/list', middleware.verifyToken, bookingController.listBookings)

router.post('/calculate-price', middleware.verifyToken, bookingController.calculatePrice)

router.get('/listByOwner', middleware.verifyToken, bookingController.listBookingsByOwner)

router.get('/:id', middleware.verifyToken, bookingController.getBookingById)

router.put('/:id/cancel', middleware.verifyToken, bookingController.cancelBooking)

router.put('/confirm/:id', middleware.verifyToken, bookingController.confirmBooking)

module.exports = router