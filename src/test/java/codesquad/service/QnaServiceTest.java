package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private User user;
    private Question question;

    @Before
    public void setUp() throws Exception {
        user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        question = new Question("title", "this is test");
        question.writeBy(user);
        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));
    }

    @Test
    public void create_question() throws Exception {

        qnaService.create(question.getWriter(), question);
        assertThat(qnaService.findById(question.getId()).get().getContents(), is(question.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_failed_when_wrong_user() {
        Question updateQuestion = new Question("hihi", "this is update");

        qnaService.update(new User("bell", "1111", "named", "ww@gmail.com"), question.getId(), updateQuestion);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_failed_when_wrong_user() throws CannotDeleteException {
        qnaService.deleteQuestion(new User("bell", "1111", "named", "ww@gmail.com"), question.getId());
    }

    @Test
    public void update_question() {
        qnaService.update(user, question.getId(), new Question("updateTitle", "this is update"));
        assertThat(qnaService.findById(question.getId()).get().getContents(), is("this is update"));
    }

    @Test
    public void delete_question() throws CannotDeleteException {
        qnaService.deleteQuestion(user, question.getId());
        assertThat(qnaService.findById(question.getId()).get().isDeleted(), is(question.isDeleted()));
    }
}
