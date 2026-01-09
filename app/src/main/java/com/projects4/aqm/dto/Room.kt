package com.projects4.aqm.dto

class Room {
    var id: String
    var title: String
    var capacity: String? = null
    var size: String? = null
    
    // New fields for device status and sensor readings
    var isFanPresent: Boolean = false
    var isLightPresent: Boolean = false
    var temperature: Double = 0.0
    var humidity: Double = 0.0

    constructor(id: String, title: String) {
        this.id = id
        this.title = title
    }

    constructor(id: String, title: String, capacity: String?, size: String?) {
        this.id = id
        this.title = title
        this.capacity = capacity
        this.size = size
        
        // dummy values for now
        this.isFanPresent = true
        this.isLightPresent = true
        this.temperature = 24.5
        this.humidity = 45.0
    }
    
    constructor(id: String, title: String, capacity: String?, size: String?, 
                isFanPresent: Boolean, isLightPresent: Boolean, temperature: Double, humidity: Double) {
        this.id = id
        this.title = title
        this.capacity = capacity
        this.size = size
        this.isFanPresent = isFanPresent
        this.isLightPresent = isLightPresent
        this.temperature = temperature
        this.humidity = humidity
    }
}
