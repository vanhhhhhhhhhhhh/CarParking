const mongoose = require('mongoose')

const bookingSchema = mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'users'
    },
    parkingId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'parkings'
    },
    status: {
        type: String,
        enum: ['pending', 'confirmed', 'cancelled', 'completed']
    },
    vehicleNumber: {
        type: String
    },
    startTime: {
        type: Date
    },
    endTime: {
        type: Date
    },
    totalPrice: {
        type: Number
    }
}, {timestamps: true})

module.exports = mongoose.model('bookings', bookingSchema)