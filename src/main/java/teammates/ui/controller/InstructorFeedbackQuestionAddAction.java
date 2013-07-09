package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionAddAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		FeedbackQuestionAttributes feedbackQuestion = extractFeedbackQuestionData();
		
		try {
			logic.createFeedbackQuestion(feedbackQuestion);	
			statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
			statusToAdmin = "Created Feedback Question for Feedback Session:<span class=\"bold\">(" +
					feedbackQuestion.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" +
					feedbackQuestion.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">Text:</span> " + feedbackQuestion.questionText;
		} catch (EntityAlreadyExistsException e) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_QUESTION_EXISTS);
			statusToAdmin = e.getMessage();
			isError = true;
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(courseId,feedbackSessionName));
	}

	private FeedbackQuestionAttributes extractFeedbackQuestionData() {
		FeedbackQuestionAttributes newQuestion =
				new FeedbackQuestionAttributes();
		
		// TODO: is instructor.email always == account.email?
		newQuestion.creatorEmail = account.email;
		newQuestion.courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		newQuestion.feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		String param;
		if((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE)) != null) {
			newQuestion.giverType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE)) != null){
			newQuestion.recipientType = FeedbackParticipantType.valueOf(param);
		}
		if((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBER)) != null){
			newQuestion.questionNumber = Integer.parseInt(param);
		}		
		newQuestion.questionText = 
				new Text(getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_TEXT));
		newQuestion.questionType = 
				FeedbackQuestionType.TEXT;
		
		newQuestion.numberOfEntitiesToGiveFeedbackTo = Const.MAX_POSSIBLE_RECIPIENTS;
		if ((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE)) != null) {
			if (param.equals("custom")) {
				if ((param = getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES)) != null) {
					if (newQuestion.recipientType == FeedbackParticipantType.STUDENTS ||
						newQuestion.recipientType == FeedbackParticipantType.TEAMS) {
						newQuestion.numberOfEntitiesToGiveFeedbackTo = Integer.parseInt(param);
					}
				}
			}
		}
		newQuestion.showResponsesTo = getParticipantListFromParams(
				getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO));				
		newQuestion.showGiverNameTo = getParticipantListFromParams(
				getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO));		
		newQuestion.showRecipientNameTo = getParticipantListFromParams(
				getRequestParam(Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO));	
		
		return newQuestion;
	}

	private List<FeedbackParticipantType> getParticipantListFromParams(String params) {
		
		List<FeedbackParticipantType> list = new ArrayList<FeedbackParticipantType>();
		
		if(params == null || params.isEmpty())
			return list;
		
		String[] splitString = params.split(",");
		
		for (String str : splitString) {
			list.add(FeedbackParticipantType.valueOf(str));
		}
		
		return list;
	}

}