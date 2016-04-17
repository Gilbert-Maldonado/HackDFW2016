/**
 * This sample demonstrates a simple skill built with the Amazon Alexa Skills Kit.
 * The Intent Schema, Custom Slots, and Sample Utterances for this skill, as well as
 * testing instructions are located at http://amzn.to/1LzFrj6
 *
 * For additional samples, visit the Alexa Skills Kit Getting Started guide at
 * http://amzn.to/1LGWsLG
 */

// Route the incoming request based on type (LaunchRequest, IntentRequest,
// etc.) The JSON body of the request is provided in the event parameter.
exports.handler = function (event, context) {
    try {
        console.log("event.session.application.applicationId=" + event.session.application.applicationId);

        /**
         * Uncomment this if statement and populate with your skill's application ID to
         * prevent someone else from configuring a skill that sends requests to this function.
         */
        /*
        if (event.session.application.applicationId !== "amzn1.echo-sdk-ams.app.b6f9336c-e795-42cf-af9d-38207d41f50d") {
             context.fail("Invalid Application ID");
        }
        */

        if (event.session.new) {
            onSessionStarted({requestId: event.request.requestId}, event.session);
        }

        if (event.request.type === "LaunchRequest") {
            onLaunch(event.request,
                event.session,
                function callback(sessionAttributes, speechletResponse) {
                    context.succeed(buildResponse(sessionAttributes, speechletResponse));
                });
        } else if (event.request.type === "IntentRequest") {
            onIntent(event.request,
                event.session,
                function callback(sessionAttributes, speechletResponse) {
                    context.succeed(buildResponse(sessionAttributes, speechletResponse));
                });
        } else if (event.request.type === "SessionEndedRequest") {
            onSessionEnded(event.request, event.session);
            context.succeed();
        }
    } catch (e) {
        context.fail("Exception: " + e);
    }
};

/**
 * Called when the session starts.
 */
function onSessionStarted(sessionStartedRequest, session) {
    console.log("onSessionStarted requestId=" + sessionStartedRequest.requestId +
        ", sessionId=" + session.sessionId);
}

/**
 * Called when the user launches the skill without specifying what they want.
 */
function onLaunch(launchRequest, session, callback) {
    console.log("onLaunch requestId=" + launchRequest.requestId +
        ", sessionId=" + session.sessionId);

    // Dispatch to your skill's launch.
    getWelcomeResponse(callback);
}

/**
 * Called when the user specifies an intent for this skill.
 */
function onIntent(intentRequest, session, callback) {
    console.log("onIntent requestId=" + intentRequest.requestId +
        ", sessionId=" + session.sessionId);

    var intent = intentRequest.intent,
        intentName = intentRequest.intent.name;

    // Dispatch to your skill's intent handlers
    if ("SetChoreIntent" === intentName || "SetHabitIntent" === intentName) {
        setTaskInSession(intent, session, callback);
    } else if ("WhatsMyChoreIntent" === intentName || "WhatsMyHabitIntent" === intentName) {
        var isChore = "WhatsMyChoreIntent" === intentName;
        getTaskFromSession(intent, session, callback, isChore);
    } else if ("AMAZON.HelpIntent" === intentName) {
        getWelcomeResponse(callback);
    } else if ("AMAZON.StopIntent" === intentName || "AMAZON.CancelIntent" === intentName) {
        handleSessionEndRequest(callback);
    } else {
        throw "Invalid intent";
    }
}

/**
 * Called when the user ends the session.
 * Is not called when the skill returns shouldEndSession=true.
 */
function onSessionEnded(sessionEndedRequest, session) {
    console.log("onSessionEnded requestId=" + sessionEndedRequest.requestId +
        ", sessionId=" + session.sessionId);
    // Add cleanup logic here
}

// --------------- Functions that control the skill's behavior -----------------------

function getWelcomeResponse(callback) {
    // If we wanted to initialize the session to have some attributes we could add those here.
    var sessionAttributes = {};
    var cardTitle = "Welcome";
    var speechOutput = "Welcome to the Earn interface. " +
        "You can set your task or ask for your current task";
    // If the user either does not reply to the welcome message or says something that is not
    // understood, they will be prompted again with this text.
    var repromptText = "Please set or ask for your current task";
    var shouldEndSession = false;

    callback(sessionAttributes,
        buildSpeechletResponse(cardTitle, speechOutput, repromptText, shouldEndSession));
}

function handleSessionEndRequest(callback) {
    var cardTitle = "Session Ended";
    var speechOutput = "Thank you for trying the earn app. Have a nice day!";
    // Setting this to true ends the session and exits the skill.
    var shouldEndSession = true;

    callback({}, buildSpeechletResponse(cardTitle, speechOutput, null, shouldEndSession));
}

/**
 * Sets the chore or habit in the session and prepares the speech to reply to the user.
 */
function setTaskInSession(intent, session, callback) {
    var intentName = intent.name;
    var setTaskTo = null;
    var repromptText = "";
    var sessionAttributes = {};
    var shouldEndSession = false;
    var speechOutput = "";
    var isChore = intentName == "SetChoreIntent";
    if(isChore) {
        setTaskTo = intent.slots.Chores;
    }
    else {
        setTaskTo = intent.slots.Habits;
    }

    if (setTaskTo !== undefined && setTaskTo.value !== undefined) {
        var currentTask = setTaskTo.value;
        sessionAttributes = createCurrentTask(currentTask, isChore);
        speechOutput = "Your ";
        if(isChore) {
            speechOutput += "chore has been set " + currentTask + ". You can ask me " +
                "what your current chore is by saying, what's my current chore?";
            repromptText = "You can ask me your current chore by saying, what's " +
                "my current chore?";
        } else {
            speechOutput += "habit has been set " + currentTask + ". You can ask me " +
            "what your current habit is by saying, what's my current habit?";
            repromptText = "You can ask me your current habit by saying, what's " +
                "my current habit?";
        }

    } else {
        if(isChore) {
            speechOutput = "I'm not sure what your current chore is. Please try again";
            repromptText = "I'm not sure what your current chore is. You can tell me your " +
                "current by saying for example, my current chore is to do my homework";
        } else {
            speechOutput = "I'm not sure what your current habit is. Please try again";
            repromptText = "I'm not sure what your current habit is. You can tell me your " +
                "current by saying for example, my current habit is to do my homework";
        }
    }

    callback(sessionAttributes,
         buildSpeechletResponse(intentName, speechOutput, repromptText, shouldEndSession));
}

function createCurrentTask(currentTask, isChore) {
    if(isChore) {
        return {
            currentChore: currentTask
        };
    } else {
        return {
            currentHabit: currentTask
        };
    }
}

function getTaskFromSession(intent, session, callback, isChore) {
    var currentTask;
    var repromptText = null;
    var sessionAttributes = {};
    var shouldEndSession = false;
    var speechOutput = "";

    if (session.attributes) {
        currentTask = session.attributes.currentTask;
    }

    if (currentTask) {
        if(isChore) {
            speechOutput = "Your current chore is " + currentTask + ". Goodbye.";
        } else {
            speechOutput = "Your current habit is " + currentTask + ". Goodbye.";
        }
        shouldEndSession = true;
    } else {
        if(isChore) {
            speechOutput = "I'm not sure what your current chore is, you can say, set my current chore " +
            " to cut the grass ";
        } else {
            speechOutput = "I'm not sure what your current habit is, you can say, my current habit " +
            " is waking up in the morning";
        }

        
    }

    // Setting repromptText to null signifies that we do not want to reprompt the user.
    // If the user does not respond or says something that is not understood, the session
    // will end.
    callback(sessionAttributes,
         buildSpeechletResponse(intent.name, speechOutput, repromptText, shouldEndSession));
}

// --------------- Helpers that build all of the responses -----------------------

function buildSpeechletResponse(title, output, repromptText, shouldEndSession) {
    return {
        outputSpeech: {
            type: "PlainText",
            text: output
        },
        card: {
            type: "Simple",
            title: "SessionSpeechlet - " + title,
            content: "SessionSpeechlet - " + output
        },
        reprompt: {
            outputSpeech: {
                type: "PlainText",
                text: repromptText
            }
        },
        shouldEndSession: shouldEndSession
    };
}

function buildResponse(sessionAttributes, speechletResponse) {
    return {
        version: "1.0",
        sessionAttributes: sessionAttributes,
        response: speechletResponse
    };
}

//SetTaskIntent set task to {wake up | Tasks}
//SetTaskIntent Can you set a task to {wake up | Tasks}

/*
Check my balance
See my current task
See the my current task that I'm working on
I finished my current task
*/