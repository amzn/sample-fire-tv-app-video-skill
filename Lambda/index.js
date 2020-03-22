/**
* Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Amazon Software License (the "License"). You may not use this file except in # compliance with the License.
* A copy of the License is located at http://aws.amazon.com/asl/
*
* or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, # WITHOUT WARRANTIES OR CONDITIONS
* OF ANY KIND, express or implied. See the License for the specific # language governing permissions and limitations under the License.
**/

// This file includes sample Lambda code for a VSK integration on Fire TV, including responses for Discovery,
// SearchAndPlay, FastForward, Channel Change, and other directive types. You can use this code as a starting
// point but then add your own logic to customize it. Note that you also should test and validate any code before using it in production.
// Amazon's recommended approach is to take directives received by the Lambda and send them directly to the app through ADM.
// Any further processing can occur within the app.

// NOTE: You should send directives from the Lambda to your app, but you should NOT try to send messages from your app back
// to the Lambda. Success responses from the Lambda are only meant to indicate that the message was received by the Lambda,
// not the app. Don't modify the responses you send back to Alexa.

// NOTE: If your Lambda is timing out, please increase the timeout duration under Advanced settings in the configuration tab.

// NOTE: Although ADM can send messages from the Lambda to your app, there is no reliable mechanism for Lambda to receive
// messages from the app back into the Lambda. Partners should not try to send messages from their app back to the lambda.

// Make sure to replace the values for CLIENT_ID (line 32), CLIENT_SECRET (line 33), and admRegistrationId (Line 298)

const AWS = require('aws-sdk');
const REQUEST = require('request');
var Fuse = require('fuse.js');

const CLIENT_ID = '<enter client id>';
const CLIENT_SECRET = '<enter client secret>';
const VIDEOS = require("video_catalog.json");

exports.handler = (event, context, callback) => {
  console.log("Lambda was invoked by Alexa");
  var name = event.directive.header.name;
  console.log("Context: " + JSON.stringify(context, null, 2));
  console.log("Directive received from Alexa: ", name, JSON.stringify(event));
  let directive = event.directive;
  if (directive) {

    if (directive.header.name == "Discover") {

      let resp = {
        event: {
          header: {
            messageId: directive.header.messageId,
            name: "Discover.Response",
            namespace: "Alexa.Discovery",
            payloadVersion: "4"
          },
          "payload": {
            "endpoints": [{
              "capabilities": [{
                  "interface": "Alexa.RemoteVideoPlayer",
                  "type": "AlexaInterface",
                  "version": "1.0"
                },
                {
                  "interface": "Alexa.PlaybackController",
                  "type": "AlexaInterface",
                  "version": "1.0"
                },
                {
                  "interface": "Alexa.SeekController",
                  "type": "AlexaInterface",
                  "version": "1.0"
                },
                {
                  "interface": "Alexa.ChannelController",
                  "type": "AlexaInterface",
                  "version": "1.0"
                }
              ],

            }]
          }
        }
      };

      // Process the directive by sending the message to app
      sendMessageToDevice(JSON.stringify(event), context).then(
        function(body) {
          console.log("Sending ", directive.header.name, " response back to Alexa: ", JSON.stringify(resp));
          return callback(null, resp);
        },
        function(err) {
          console.log('Could not send the message to the device: ' + err);
          return callback(null);
        }
      )

    } else if (
      directive.header.name == "Play" ||
      directive.header.name == "Resume" ||
      directive.header.name == "Pause" ||
      directive.header.name == "Next" ||
      directive.header.name == "Previous" ||
      directive.header.name == "Rewind" ||
      directive.header.name == "FastForward") {

      // Partner-specific logic to handle the directives goes here

      // Below, we send a "success" response back to Alexa, indicating the directive was received by the Lambda
      let resp = {
        event: {
          "header": {
            "messageId": "not-required",
            "correlationToken": "not-required",
            "name": "Response",
            "namespace": "Alexa",
            "payloadVersion": "3"
          },
          "endpoint": {
            "scope": {
              "type": "DirectedUserId",
              "directedUserId": "not-required"
            },
            "endpointId": "not-required"
          },
          "payload": {}
        }
      };

      // Process the directive by sending the message to app
      sendMessageToDevice(JSON.stringify(event), context).then(
        function(body) {
          console.log("Sending ", directive.header.name, " response back to Alexa: ", JSON.stringify(resp));
          return callback(null, resp);
        },
        function(err) {
          console.log('Could not send the message to the device: ' + err);
          return callback(null);
        }
      )

    } else if (directive.header.name == "SearchAndPlay" || directive.header.name == "SearchAndDisplayResults") {

     console.log("Search started");

      var options = {
        shouldSort: true,
        includeScore: true,
        includeMatches: false,
        threshold: 0.6,
        location: 0,
        distance: 100,
        maxPatternLength: 32,
        minMatchCharLength: 1,
        keys: [{
          name: 'title',
          weight: 0.5
        }, {
          name: 'genre',
          weight: 0.4
        },
        {
          name: 'description',
          weight: 0.1
        }]
      };
      var fuse = new Fuse(VIDEOS, options)

      let searchTerm = directive.payload.entities[0].value;
      console.log("Search Term: " + searchTerm);
      var searchResults = fuse.search(searchTerm);

      event.directive.payload.searchResults = searchResults;
      var stringifySearchResults = JSON.stringify(searchResults);
      console.log("Search Results: " + stringifySearchResults);


      // Below, we send a "success" response back to Alexa, indicating the directive was received by the Lambda
      let resp = {
        event: {
          "header": {
            "messageId": "not-required",
            "correlationToken": "not-required",
            "name": "Response",
            "namespace": "Alexa",
            "payloadVersion": "3"
          },
          "endpoint": {
            "scope": {
              "type": "DirectedUserId",
              "directedUserId": "not-required"
            },
            "endpointId": "not-required"
          },
          "payload": {}
        }
      };

      // Process the directive by sending the message to app
      sendMessageToDevice(JSON.stringify(event), context).then(
        function(body) {
          console.log("Sending ", directive.header.name, " response back to Alexa: ", JSON.stringify(resp));
          return callback(null, resp);
        },
        function(err) {
          console.log('Could not send the message to the device: ' + err);
          return callback(null);
        }
      )
    } else if (directive.header.name == "AdjustSeekPosition") {

      // Partner-specific logic to handle the AdjustSeekPosition directive goes here

      // Below, we send a "success" response back to Alexa, indicating the directive was received by the lambda
      let resp = {
        event: {
          "header": {
            "messageId": "not-required",
            "correlationToken": "not-required",
            "name": "StateReport",
            "namespace": "Alexa.SeekController",
            "payloadVersion": "3"
          },
          "endpoint": {
            "scope": {
              "type": "DirectedUserId",
              "directedUserId": "not-required"
            },
            "endpointId": "not-required"
          },
          "payload": {
            "properties": [{
              "name": "positionMilliseconds",
              "value": 0
            }]
          }
        }
      };

      // Process the directive by sending the message to app
      sendMessageToDevice(JSON.stringify(event), context).then(
        function(body) {
          console.log("Sending ", directive.header.name, " response back to Alexa: ", JSON.stringify(resp));
          return callback(null, resp);
        },
        function(err) {
          console.log('Could not send the message to the device: ' + err);
          return callback(null);
        }
      )

    } else if (directive.header.name == "ChangeChannel") {

      // Partner-specific logic to handle the ChangeChannel directive goes here
      // Code for sending this directive from the lambda to the app goes here

      // Below, we send a "success" response back to Alexa, indicating the directive was received by the lambda
      let resp = {
        context: {
          "properties": [{
            "namespace": "Alexa.ChannelController",
            "name": "channel",
            "value": {
              "number": "1234",
              "callSign": "not-required",
              "affiliateCallSign": "not-required"
            },
            "timeOfSample": "2017-02-03T16:20:50.52Z",
            "uncertaintyInMilliseconds": 0
          }]
        },
        event: {
          "header": {
            "messageId": "not-required",
            "correlationToken": "not-required",
            "name": "Response",
            "namespace": "Alexa",
            "payloadVersion": "3"
          },
          "endpoint": {
            "endpointId": "not-required"
          },
          "payload": {}
        }
      };

      // Process the directive by sending the message to app
      sendMessageToDevice(JSON.stringify(event), context).then(
        function(body) {
          console.log("Sending ", directive.header.name, " response back to Alexa: ", JSON.stringify(resp));
          return callback(null, resp);
        },
        function(err) {
          console.log('Could not send the message to the device: ' + err);
          return callback(null);
        }
      )

    } else if (directive.header.name == "AcceptGrant")
    {
        console.log("Directive received from Alexa", directive.header.name)
        let resp = {
            "event": {
                "header": {
                    "messageId": "abc-123-def-456",
                    "namespace": "Alexa.Authorization",
                    "name": "AcceptGrant.Response",
                    "payloadVersion": "3"
                    },
            "payload": {}
          }
        };
        console.log("Sending ", directive.header.name, " response back to Alexa: ", JSON.stringify(resp));
        callback(null, resp);

    } else {
      console.log("Directive is not supported. Ignoring it.");
      callback(null, 'Directive is not supported. Ignoring it.');
    }
  } else {
    console.log('Directive is null. Ignoring it.');
    callback(null, 'Directive is null. Ignoring it.');
  }
};



// This function provides sample code to send a directive from the Lambda to a Fire TV app via ADM. Partners should
// combine the sample code in this function with their own client id, client secret, etc
// to create a full implementation.
//
// Prerequisites:
//
// 1) Understand Amazon Device Messaging (ADM),which lets you send messages to Amazon devices that run your app.
// https://developer.amazon.com/docs/adm/overview.html
//
// 2) A FireTV application that is integrated with ADM.
// https://developer.amazon.com/docs/adm/set-up.html
function fetchAccessToken(event, context) {
  const ADM_AUTH_URL = 'https://api.amazon.com/auth/O2/token';
  var responseToAlexa = 'This is a dummy response. Replace it with your own value.';

  return new Promise(function(resolve, reject) {

    // Here, we get the accessToken from ADM using your credentials. If it succeeds, we call the sendMessageToDevice()
    // function. We use the "request" module for sending the request. ADM documentation regarding request headers:
    // https://developer.amazon.com/docs/adm/request-access-token.html
    REQUEST({
      url: ADM_AUTH_URL,
      method: 'POST',
      auth: {
        user: CLIENT_ID,
        pass: CLIENT_SECRET
      },
      form: {
        'grant_type': 'client_credentials',
        'scope': 'messaging:push'
      }
    }, function(err, res) {
      if (err || res.statusCode != 200) {
        console.log("Unable to retrieve access token: " + err + " Response code: " + res.statusCode);
        reject("No access token!");
      } else {
        console.log('Got Access Token Type:' + JSON.parse(res.body).token_type + ' Expires In:' + JSON.parse(res.body).expires_in);
        resolve(JSON.parse(res.body).access_token);
      }
    });
  });

}


// This function fetches an access token before sending a message to your app via ADM. We reference your app instance
// via the ADM Registration ID. In production, partners should read  ADM Registration IDs from directives, and
// dynamically route messages to the correct app, rather than hardcoding.

// This method is a blocking method and will not respond back to Alexa till it gets a response back from the app.
// If response time becomes an issue try sending message to device in async mode
function sendMessageToDevice(message, context) {
  const UTIL = require('util');
  const ADM_MESSAGE_URL = 'https://api.amazon.com/messaging/registrations/%s/messages';
  //var admRegistrationId = '<enter app ADM registration id>';
  var admRegistrationId = JSON.parse(message).directive.endpoint.cookie.applicationInstanceId;
  console.log ("ADM Registration ID: " + admRegistrationId);


  return fetchAccessToken().then((accessToken) => {

    // Construct Request Headers per ADM specification:
    // https://developer.amazon.com/docs/adm/send-message.html
    var postData = {
      data: {
        'message': message,
        'timeStamp': new Date().valueOf().toString()
      },
      consolidationKey: 'ADM_Enqueue_Sample',
      expiresAfter: 3600
    }
    var url = UTIL.format(ADM_MESSAGE_URL, admRegistrationId);
    var options = {
      method: 'post',
      body: postData,
      json: true,
      url: url,
      auth: {
        'bearer': accessToken
      },
      headers: {
        'content-Type': 'application/json',
        'accept': 'application/json',
        'X-Amzn-Type-Version': 'com.amazon.device.messaging.ADMMessage@1.0',
        'X-Amzn-Accept-Type': 'com.amazon.device.messaging.ADMSendResult@1.0'
      }
    };

    return new Promise(function(resolve, reject) {
      console.log('Calling device...');
      console.log ("Sending message to app: " + JSON.stringify(message));
      // Use "request" module to send the request to ADM-server
      REQUEST(options, function (err, res, body) {
          if (err || res.statusCode != 200) {
              console.log('Error sending message to the app: ' + err + ' Response code: ' + res.statusCode);
              console.log('Response: ' + JSON.stringify(body));
              reject(null);
          } else {
              console.log('Response from the app: ' + JSON.stringify(body));
              resolve(body);
          }
      })

    });

  });


}
