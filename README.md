# HummingBird

HummingBird is navigation system useful for bus rider.

##Overview

Image in a crowded bus, in rainy day, or in a foreign country, you can’t see or understand next
stop sign, or can’t see outside where you are. HummingBird provide the crucial data, like
estimated arrival time, and distance for your next checkpoints, and give you notification when
approaching stop you need to get off.

##Intended User

Travelers who want to travel freely via transit system.

## Features

* Trip Planning
* Navigation
* StreetView

## Pre-requisites

* Android SDK v23
* Android Build Tools v23.0.1
* Android Support Repository
* Google Play Services

##Libraries

* [Retrofit](http://square.github.io/retrofit/), A type-safe HTTP client for Android and Java
* [Schematic](https://github.com/SimonVT/schematic), Automatically generate a ContentProvider backed by an SQLite database
* [SlideDateTimePicker](https://github.com/jjobes/SlideDateTimePicker),  Android library that displays a single DialogFragment 
in which the user can select a date and a time
* [Butterknife](http://jakewharton.github.io/butterknife/), Field and method binding for Android views
* [Google Maps Android API utility library](https://github.com/googlemaps/android-maps-utils),  library contains classes that are 
useful for a wide range of applications using the Google Maps Android API

##Google Map API key and Google Map Server key

Google map API key and Google map server key are defined in the file **google_maps_api.xml** 
under the folder **app/src/release/res/values** and **app/src/debug/res/values**. The API key can't be shared in public.  
Please create the file **google_maps_api.xml** with the following file template.

```
<resources>
  <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">[KEY DEFINED HERE]</string>
  <string name="google_maps_server_key" templateMergeStrategy="preserve" translatable="false">[KEY DEFINED HERE]</string>
</resources/>
```


