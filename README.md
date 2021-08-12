# Readme

This sample-fire-tv-app-video-skill project contains a basic Android TV app (in the VSKFireTV-sample-app folder) that incorporates a working video skill to play videos on Fire TV. To accomplish this, the app incorporates the Alexa Client Library and Amazon Device Messaging (ADM). Once configured, the app plays up a video when you ask Alexa for the video by title, such as by saying "Alexa, watch [movie title]" or "Alexa, find [movie title]." You'll still need to configure the sample app with your package name and video skill, as well as perform other steps to set up the app. Note that the code doesn't play the actual content asked for, just some generic test videos.

The sample app will allow you to see a directive pushed to the app. This preview can give you more of a sense of how the video skill will interact with your app. The sample app does not show logic about how to handle the directives to perform specific actions in the app -- you'll need to work out much of the logic on your own based on your own unique code. 

Additionally, this sample app is meant as a companion to the documentation, not as a starting point or template for your own app. Almost every video partner that integrates the VSK already has a custom-developed app. The sample app simply provides some context for some of the integration instructions. For details on setting up the sample app, see the VSK Fire TV documentation on the Amazon Developer Portal:

* [Video Skills Kit (VSK) for Fire TV Apps Overview](https://developer.amazon.com/docs/video-skills-fire-tv-apps/introduction.html)
* [Step 2: Set Up the Sample Fire TV App (VSK Fire TV)](https://developer.amazon.com/docs/video-skills-fire-tv-apps/set-up-sample-app.html)

Additionally, the sample-fire-tv-app-video-skill project contains a sample Lambda function (in the Lambda folder) for handling the directives. Lambda is a serverless computing technology that can run functions in the cloud. The Lambda folder contains an `index.js` file, which contains the Lambda function coded in Node, and a `package.json` file to download Node modules needed to generate the Lambda code as a deployment package. You will upload this Lambda into your Lambda configuration in AWS. When your video skill receives utterances, it will invoke this Lambda function, and your Lambda function will in turn pass the received payload from the Alexa directives to your Fire TV app through ADM. 

The Lambda folder also contains `video_catalog.json` that provides an example of a data source for the video content. For more information on configuring this Lambda and observing the logs in Cloudwatch, see the following: 

* [Step 6: Create and Deploy a Lambda Package (VSK Fire TV)](https://developer.amazon.com/docs/video-skills-fire-tv-apps/create-and-deploy-lambda-package.html)
* [Step 8: Test Your Skill and Observe Logs in CloudWatch (VSK Fire TV)](https://developer.amazon.com/docs/video-skills-fire-tv-apps/test-your-skill-and-observe-logs.html)

For detailed instructions on setting up the sample app, see the cloudside implementation [documentation](https://developer.amazon.com/docs/video-skills-fire-tv-apps/create-video-skill-and-set-up-devices.html)

An abbreviated version of the instructions are as follows:

1. Clone this project to your local workspace using `git clone https://github.com/amzn/sample-fire-tv-app-video-skill.git`.
2. Create a video skill in [developer portal](https://developer.amazon.com), we will fill additional skill details in later stages. Note down the video skillId for later use.
3. Open this project code in Android Studio and replace the default package name `com.example.vskfiretv` in such a way that it is unique.
4. Customize the video skill by going to `app -> java -> com -> example -> vskfiretv -> MainActivity` (the path will differ based on how you customized the package name) and assign your video skillId to the "alexaSkillId" variable at line 89
5. Integrate Alexa Client Library by importing its AAR file in your sample app. You can download the latest client AAR file [here](https://developer.amazon.com/docs/video-skills-fire-tv-apps/integrate-alexa-client-library.html).
6. Integrate Amazon Device Messaging (ADM) by including its JAR file in your sample app. Create a libs folder inside the app folder, i.e `sample-fire-tv-app-video-skill/VSKFireTV-sample-app/app/libs/`. Add the ADM JAR file inside the libs folder. You can download the latest ADM JAR file [here](https://developer.amazon.com/docs/adm/overview.html).
7. Rebuild the project and make sure the build is fine.
8. Create a custom key to sign your app. You can create a custom key in the android studio by going to `Build -> Generate Signed Bundle/APK -> Click Next -> Create New Key`.
9. Automatically sign your app with the custom key by making the debug/release profile to use this key. Goto `File -> Project Structure -> Modules -> Signing Configs -> Click + button` to create a new signing config called `firetv` by providing your custom key information. Then `Click Apply -> Ok`.
10. Get the MD5 and SHA-256 values from your custom key by running "signingReport" in Android Studio.
11. Create a security profile in the Amazon developer console (Settings > Security Profiles) and enter the MD5 and SHA-256 values of your customer key in "Android/Kindle Settings." Then generate a new API key and note it down. Also not down the Client ID and Client Secret present in the "Web Settings" tab.
12. Enable Login with Amazon for your Security Profile
13. Enter the API key in your sample app by going to `api_key.txt` file in the asset folder and inserting your API key. This will enable your app to receive messages from Amazon Device Messaging (ADM).
14. Generate a release APK file (signed with your custom key) and upload it into the Amazon developer console by creating a new app in the appstore.
15. Attach the security profile to your app so that your app is authorized on the Fire TV. 
16. Configure your lambda present in this project by updating Client ID and Client Secret in index.js at lines 32 & 33. You can get Client ID and Client Secret from Step 11.
17. Create a lambda deployment project by running this command `zip -r firetv-lambda.zip .` in the project Lambda folder
18. Create a lambda function(with basic lambda execution role) in AWS for your video skill. Once your lambda function is created, in the trigger configuration section add a trigger by selecting Alexa Smart Home and provide your video skillId(created in step 2). This would let your video skill to send directives to your lambda function. Note down the ARN address for your lambda which will be used later.
19. Upload your lambda code to your lambda function using the zip file `firetv-lambda.zip` created in step 17.
20. Update your video skill in the developer console by providing your lambda ARN address and also by selecting your FireTv application that you created earlier in Step 14. You can use "tms" for catalog name. Fill the required information(like Distribution tab) and save your video skill.
21. Get a Fire TV cube device and connect your computer to it through adb. (See [Connect to Fire TV through adb](https://developer.amazon.com/docs/fire-tv/connecting-adb-to-device.html).)
22. Inside Android Studio, run the app on your connected Fire TV device. You can monitor adb logs using the following commands when running the app. You will see that the sample app's capabilities are successfully reported in the adb logs.
    * `adb logcat | grep "AlexaClient" -i`
23.  Open a terminal and run `adb logcat | grep "VSKFireTVMessageHandler" -i`. Then say the test utterance "Alexa, watch Superman" and look for a `SearchAndPlay` directive to get pushed to your app. You will see a random video being played in the sample app, and you can look at the directive payload in the adb logs. 
24. You can test other utterances such as "Alexa, search for comedies," "Alexa, pause" (while the video is playing), "Alexa, rewind," "Alexa fast forward by 25 seconds," and so on.
