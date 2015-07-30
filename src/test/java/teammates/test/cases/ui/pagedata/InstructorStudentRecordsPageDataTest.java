package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Sanitizer;
import teammates.ui.controller.InstructorStudentRecordsPageData;

public class InstructorStudentRecordsPageDataTest {

    private AccountAttributes sampleAccount;
    private StudentAttributes sampleStudent;
    private String sampleCourseId;
    private String sampleShowCommentBox;
    private StudentProfileAttributes sampleStudentProfile;
    private InstructorAttributes sampleInstructor;
    private List<CommentAttributes> sampleComments;
    private List<String> sampleSessionNames;

    private InstructorStudentRecordsPageData isrpd;

    @SuppressWarnings("deprecation")
    private void initializeData() {
        sampleAccount = new AccountAttributes();

        sampleStudent = new StudentAttributes();
        sampleStudent.name = "Sample name<><>";
        sampleStudent.team = "Sample team";
        sampleStudent.section = "Sample section";
        sampleStudent.email = "e@mail.com";

        sampleCourseId = "Sample courseId";

        sampleShowCommentBox = "student";

        sampleStudentProfile = new StudentProfileAttributes();

        sampleInstructor = new InstructorAttributes();

        sampleComments = new ArrayList<CommentAttributes>();
        CommentAttributes sampleComment = new CommentAttributes();
        sampleComment.showCommentTo = new ArrayList<CommentParticipantType>();
        sampleComments.add(sampleComment);

        sampleSessionNames = new ArrayList<String>();
        sampleSessionNames.add("Session name");
    }

    @Test
    public void testAll() {
        initializeData();
        isrpd = new InstructorStudentRecordsPageData(sampleAccount, sampleStudent, sampleCourseId,
                                                     sampleShowCommentBox, sampleStudentProfile,
                                                     sampleComments, sampleSessionNames,sampleInstructor);
        assertEquals(sampleAccount.googleId, isrpd.getGoogleId());
        assertEquals(sampleCourseId, isrpd.getCourseId());
        assertEquals(Sanitizer.sanitizeForHtml(sampleStudent.name), isrpd.getStudentName());
        assertEquals(sampleStudent.email, isrpd.getStudentEmail());
        assertEquals(sampleShowCommentBox, isrpd.getShowCommentBox());
        assertNotNull(isrpd.getStudentProfile());
        assertNotNull(isrpd.spa);
        
        // test with null student profile
        isrpd = new InstructorStudentRecordsPageData(sampleAccount, sampleStudent, sampleCourseId,
                                                     sampleShowCommentBox, null, sampleComments,
                                                     sampleSessionNames,sampleInstructor);
        assertNull(isrpd.getStudentProfile());
        assertNull(isrpd.spa);
    }

}
