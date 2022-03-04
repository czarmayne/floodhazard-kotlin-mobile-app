package com.the42studios.floodhazard.entity

class Location {

    var id: Int = 0
    var name: String = ""
    var status:String = "N"
    var details:String = ""


    override fun toString(): String {
        return "Location(id='$id', name='$name', status='$status', details='$details')"
    }


}
