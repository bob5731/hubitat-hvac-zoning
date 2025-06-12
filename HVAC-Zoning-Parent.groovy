definition(
    name: "HVAC Zoning - Parent",
    namespace: "example",
    author: "You",
    description: "Parent app to manage HVAC zoning",
    category: "Green Living",
    iconUrl: "https://raw.githubusercontent.com/djgutheinz/Media/master/blank.png",
    iconX2Url: "https://raw.githubusercontent.com/djgutheinz/Media/master/blank.png"
)

preferences {
    page(name: "mainPage")
}

def mainPage() {
    dynamicPage(name: "mainPage", title: "HVAC Zoning App", install: true, uninstall: true) {
        section("Child Apps") {
            app(name: "childApps", appName: "HVAC Zoning - Child", namespace: "example", multiple: true)
        }
        section("Thermostat Control") {
            input "thermostat", "capability.thermostat", title: "Thermostat Device", required: true
        }
    }
}

def installed() {
    log.debug "Installed parent app with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated parent app with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.debug "Initializing parent app"
}

def reportRoomStatus(roomName, isSet, setPoint, currentTemp) {
    log.debug "Room '${roomName}' reported: Current Temp = ${currentTemp}, Set Point = ${setPoint}, Is Set = ${isSet}"
    def allSet = getChildApps().every { child ->
        child.currentTemperature <= child.roomSetPoint
    }
    if (!allSet) {
        thermostat.setCoolingSetpoint(thermostat.currentValue("coolingSetpoint"))
        thermostat.cool()
    } else {
        thermostat.off()
    }
}