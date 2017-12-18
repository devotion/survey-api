db.createUser({ user: 'user', pwd: 'password', roles: [ {role:'readWrite', db:'results'} ]});

db.surveyresults.insertMany(
[
/* 1 */
{
    "_id" : ObjectId("59a84b114d27fc00074384b6"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:44:49.023Z")
},

/* 2 */
{
    "_id" : ObjectId("59a84b554d27fc00074384b8"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:45:57.207Z")
},

/* 3 */
{
    "_id" : ObjectId("59a84b604d27fc00074384ba"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:46:08.813Z")
},

/* 4 */
{
    "_id" : ObjectId("59a84b614d27fc00074384bc"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:46:09.412Z")
},

/* 5 */
{
    "_id" : ObjectId("59a84b614d27fc00074384be"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:46:09.821Z")
},

/* 6 */
{
    "_id" : ObjectId("59a84b6f4d27fc00074384c0"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:46:23.210Z")
},

/* 7 */
{
    "_id" : ObjectId("59a84b704d27fc00074384c2"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:46:24.817Z")
},

/* 8 */
{
    "_id" : ObjectId("59a84b974d27fc00074384c4"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:47:03.177Z")
},

/* 9 */
{
    "_id" : ObjectId("59a84b974d27fc00074384c6"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:47:03.610Z")
},

/* 10 */
{
    "_id" : ObjectId("59a84c4b4d27fc00074384c8"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:50:03.713Z")
},

/* 11 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384ca"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:50:04.351Z")
},

/* 12 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384cc"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:50:04.549Z")
},

/* 13 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384ce"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:50:04.730Z")
},

/* 14 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384d0"),
    "_class" : "com.draganlj.survey.capture.model.SurveyResult",
    "surveyId" : "1",
    "user" : {
        "_class" : "com.devotion.capture.model.AnonymousUser",
        "uniqueId" : "172.21.0.5"
    },
    "submitDate" : ISODate("2017-08-31T17:50:04.865Z")
}
]);

db.questionanswers.insertMany([
/* 1 */
{
    "_id" : ObjectId("59a84b114d27fc00074384b7"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b114d27fc00074384b6",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae83",
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 2 */
{
    "_id" : ObjectId("59a84b554d27fc00074384b9"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b554d27fc00074384b8",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae84",
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 3 */
{
    "_id" : ObjectId("59a84b604d27fc00074384bb"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b604d27fc00074384ba",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae84",
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 4 */
{
    "_id" : ObjectId("59a84b614d27fc00074384bd"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b614d27fc00074384bc",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae84",
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 5 */
{
    "_id" : ObjectId("59a84b614d27fc00074384bf"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b614d27fc00074384be",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae84",
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 6 */
{
    "_id" : ObjectId("59a84b6f4d27fc00074384c1"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b6f4d27fc00074384c0",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 7 */
{
    "_id" : ObjectId("59a84b704d27fc00074384c3"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b704d27fc00074384c2",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae84"
    ]
},

/* 8 */
{
    "_id" : ObjectId("59a84b974d27fc00074384c5"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b974d27fc00074384c4",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
},

/* 9 */
{
    "_id" : ObjectId("59a84b974d27fc00074384c7"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84b974d27fc00074384c6",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
},

/* 10 */
{
    "_id" : ObjectId("59a84c4b4d27fc00074384c9"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84c4b4d27fc00074384c8",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
},

/* 11 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384cb"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84c4c4d27fc00074384ca",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
},

/* 12 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384cd"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84c4c4d27fc00074384cc",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
},

/* 13 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384cf"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84c4c4d27fc00074384ce",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
},

/* 14 */
{
    "_id" : ObjectId("59a84c4c4d27fc00074384d1"),
    "_class" : "com.draganlj.survey.capture.model.QuestionAnswer",
    "surveyResultId" : "59a84c4c4d27fc00074384d0",
    "surveyId" : "1",
    "questionId" : 0,
    "answerIds" : [
        "59a849ef1698a3426ce6ae82"
    ]
}
]);