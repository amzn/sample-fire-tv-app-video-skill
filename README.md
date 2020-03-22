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


