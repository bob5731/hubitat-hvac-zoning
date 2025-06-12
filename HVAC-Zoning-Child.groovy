// HVAC Zoning - Child

definition(
    name: "HVAC Zoning - Child",
    namespace: "example",
    author: "Your Name",
    description: "Single HVAC Zone",
    category: "Green Living",
    iconUrl: "https://raw.githubusercontent.com/hubitat/example-icons/main/vent.png",
    iconX2Url: "https://raw.githubusercontent.com/hubitat/example-icons/main/vent@2x.png",
    parent: "example.HVAC Zoning - Parent"
)

preferences {
    section("Room Details") {
        input "roomName", "text", title: "Room Name", required: true
        input "setPoint", "decimal", title: "Temperature Set Point", required: true
    }
    section("Devices") {
        input "tempSensor", "capability.temperatureMeasurement", title: "Temperature Sensor", required: true
        input "vent", "capability.switchLevel", title: "Vent (Dimmer Switch)", required: true
    }
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {
    runEvery5Minutes("checkTemperature")
    checkTemperature()
}

def checkTemperature() {
    def currentTemp = tempSensor.currentTemperature as Double
    def targetTemp = setPoint as Double
    def ventLevel = vent.currentLevel as Integer ?: 0

    if (currentTemp >= targetTemp) {
        if (ventLevel != 100) {
            vent.setLevel(100)
            log.debug "$roomName: Temp $currentTemp >= setpoint $targetTemp, opening vent"
        }
        parent.reportRoomStatus(roomName, true, targetTemp)
    } else {
        if (ventLevel != 0) {
            vent.setLevel(0)
            log.debug "$roomName: Temp $currentTemp < setpoint $targetTemp, closing vent"
        }
        parent.reportRoomStatus(roomName, false, targetTemp)
    }
}