package com.the42studios.floodhazard.entity

class Sensitivity {

    var id: Int = 0
    var location : String = ""
    var intensity : String = ""
    var date : String = ""
    var hour : Int = 0
    var sensitivityLevel : String = ""
    var sensitivityDetail : String = ""

    override fun toString(): String {
        return "Sensitivity(id=$id, location='$location', intensity='$intensity', date='$date', hour=$hour, sensitivityLevel='$sensitivityLevel', sensitivityDetail='$sensitivityDetail')"
    }


}