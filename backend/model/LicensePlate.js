const mongoose = require('mongoose')

const licensePlateSchema = mongoose.Schema({
    userId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'users'
    },
    licensePlate: {
        type: String
    }
}, {timestamps: true})

module.exports = mongoose.model('licensePlates', licensePlateSchema)