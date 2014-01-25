Pentaho Data Integration Android Push Notifications
=============================

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/latinojoel/pdi-android-pushnotifications/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

This is a plugin that allows to you send push notifications from **Pentaho Data Integration** to any **android** application that enable GCM service.
The motivation for developed this plugin is for you, as ETL Developer, receive a quickly notifications about state of ETL executions in any place (than faster email), you can use my app [PDI Manager Android App](https://play.google.com/store/apps/details?id=com.latinojoel.pdimanager). However, this plugin can be use for other's scenarios.


Compile
=============================
**Run Maven command-line:** mvn clean install


Instalation
===========
1. You need Pentaho Data Integration installed;
2. Download  the plugin from sourceforce, you can find in this [link](http://sourceforge.net/projects/pentaho-plugins/files/Pentaho%20Data%20Integration/PDI%20Android%20Push%20Notifications);
3. Stop your Pentaho Data Integration if it's running;
4. Uncompress *AndroidPushNotification* file;
5. Copy *AndroidPushNotification* folder to *<pdi-folder-installation>/plugins/steps*; 
6. Start your Data Integration and enjoy.

How use plugin
=======
After installed plugin, you can find the step on Utility category of transformation, drag and drop the step to the canvas and do a double click on the step. You can see two tabs *Main Options* and *Properties*, the *Main Options* is more about data of push notification package and *Properties* is related of configuration to send push notifications.


**Parameters**

* **Regestration Id** - An ID issued by the GCM servers to the Android application that allows it to receive messages. Once the Android application has the registration ID, it sends it to the 3rd-party application server (Pentaho Data Integration), which uses it to identify each device that has registered to receive messages for a given Android application. In other words, a registration ID is tied to a particular Android application running on a particular device. Required.
* **Collapse Key** - An arbitrary string (such as "Updates Available") that is used to collapse a group of like messages when the device is offline, so that only the last message gets sent to the client. This is intended to avoid sending too many messages to the phone when it comes back online. Note that since there is no guarantee of the order in which messages get sent, the "last" message may not actually be the last message sent by the application server. Optional.
* **Delay while idle** - If included, indicates that the message should not be sent immediately if the device is idle. The server will wait for the device to become active, and then only the last message for each collapse_key value will be sent. Optional. The default value is false, and must be a JSON boolean.
* **Time to live** - How long (in seconds) the message should be kept on GCM storage if the device is offline. Optional.
* **Restricted package name** - A string containing the package name of your application. When set, messages will only be sent to registration IDs that match the package name. Optional.
* **Dry run** - If included, allows developers to test their request without actually sending a message. Optional. The default value is false, and must be a JSON boolean.
* **Message Data** - Payload data, expressed as parameters prefixed with data. and suffixed as the key
* **API Key** - An API key that is saved on the 3rd-party application server that gives the application server authorized access to Google services. The API key is included in the header of POST requests that send messages.
* **Response field** - Response of sending push. See [Response format](http://developer.android.com/google/gcm/gcm.html#response).
* **Push notification encoding** - Request encoding.
* **Retrying in case of unavailability** - If retrying in case of unavailability
* **Retries number** - Number of retries
* **Delay before last retry** - Delay between of retries.



PDI Manager Android App
=======================
At this moment, PDI Manager is configured to receive four properties inside the push message:
* Status
* Project
* Date
* Data. 

Use the follow **API Key**: AIzaSyAh7Nf-N7bE4xIwsVb7nk4mmls_yEQwZQA

**How you get your Registragion ID**
In the main screen you should be click on menu button and choose the Registration ID menu option. In the new Registration ID screen, you can share your registration ID for use it on android push notificatio step.


Interested Links
=======
* [PDI Manager Android App](https://play.google.com/store/apps/details?id=com.latinojoel.pdimanager)
* [Google Cloud Messaging](http://developer.android.com/google/gcm/)
* [SourceForge Pentaho Plugins](http://sourceforge.net/projects/pentaho-plugins/)
* [Joel Latino Author - About](https://about.me/latinojoel)
* [Joel Latino Author - Linkedin](http://pt.linkedin.com/in/latinojoel)
* [Joel Latino Author - Blog](http://joel-latino.blogspot.com/)



[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/d849a18c46ab364013deb6ddec98bc48 "githalytics.com")](http://githalytics.com/latinojoel/pdi-android-pushnotifications)
