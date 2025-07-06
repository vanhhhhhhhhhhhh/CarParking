const mongoose = require('mongoose')

const userSchema = mongoose.Schema({
    phone: {
        type: String,
    },
    password: {
        type: String
    },
    role: {
        type: String,
        enum: ['admin', 'user', 'owner'],
        default: 'user'
    },
    fullName: {
        type: String
    },
}, {timestamps: true})

module.exports = mongoose.model('users', userSchema)