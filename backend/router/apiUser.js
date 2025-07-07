const router = require('express').Router()
const userController = require('../controller/userController')
const middleware = require('../controller/middleware')


router.post('/register', userController.register)
router.post('/login', userController.login)

module.exports = router