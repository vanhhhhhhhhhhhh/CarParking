const express = require('express')
const mongoose = require('mongoose')
const cors = require('cors')
require('dotenv').config()
const apiUser = require('./router/apiUser')



const hostname = process.env.HOSTNAME
const port = process.env.PORT
const mongoURL = process.env.URL
const dbName = process.env.DBNAME 

const app = express()
app.use(express.json())


mongoose.connect(`${mongoURL}${dbName}`, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
})
    .then(() => console.log('Connected to MongoDB successfully'))
    .catch((err) => console.error('MongoDB connection error:', err));

app.use(cors());

// AUTHENTICATION
app.use('/auth', apiUser)

app.listen(port, () => {
    console.log(`Server is running on http://${hostname}:${port}`);
})