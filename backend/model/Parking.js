const mongoose = require('mongoose')

const parkingSchema = mongoose.Schema({
    name: {
        type: String
    },
    address: {
        type: String
    },
    location: {
        type: {
            type: String,
            enum: ['Point'],
            default: 'Point'
        },
        coordinates: {
            type: [Number], // [longitude, latitude]
        }
    },
    totalSlots: {
        type: Number
    },
    availableSlots: {
        type: Number
    },
    pricePerHour: {
        type: Number
    },
    openTime: {
        type: String
    },
    closeTime: {
        type: String
    },
    imageUrl: {
        type: String
    },
    ownerId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'users'
    }
}, { timestamps })

module.exports = mongoose.model('parkings', parkingSchema)