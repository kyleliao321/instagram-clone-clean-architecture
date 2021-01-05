package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.data.model.LoginCredentialDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.PostDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileDataModel
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.*
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.AccountServices
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.PostServices
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.UserServices
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBe
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import java.net.HttpURLConnection

@RunWith(JUnit4::class)
class RemoteDataSourceImplTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var accountServices: AccountServices

    @MockK(relaxed = true)
    internal lateinit var userServices: UserServices

    @MockK(relaxed = true)
    internal lateinit var postServices: PostServices

    private lateinit var remoteDataSourceImpl: RemoteDataSource

    private val mockUserId = "mockUserId"
    private val mockUserName = "mockUserName"
    private val mockPassword = "mockPassword"
    private val mockUserNameKeyword = "mocKeyword"

    private val mockUserProfile = UserProfileDataModel(
        id = mockUserId,
        userName = mockUserName,
        alias = "mockAlias",
        description = "mockDescription",
        imageSrc = "mockImageSrc",
        postNum = 0,
        followingNum = 0,
        followerNum = 0
    )

    private val mockPostId = "mockPostId"

    private val mockPost = PostDataModel(
        id = mockPostId,
        description = "mockDes",
        location = "mockLocation",
        timestamp = "mockTimestamp",
        imageSrc = "mockImageSrc",
        postedUserId = mockUserId
    )

    private val mockLoginCredential = LoginCredentialDataModel(
        jwt = "mockJwt",
        userId = mockUserId
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        remoteDataSourceImpl = RemoteDataSourceImpl(accountServices, userServices, postServices)
    }

    @Test
    fun `userRegister should return success when accountServices registerNewAccountAsync return status HTTP_CREATED`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_CREATED
        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `userRegister should return duplicated username failure when accountServices registerNewAccountAsync return status HTTP_CONFLICT`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_CONFLICT
        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Failure(Failure.DuplicatedUserName)
    }

    @Test
    fun `userRegister should return server error failure when accountServices getUserProfileAsync return status other than HTTP_CREATED or HTTP_CONFLICT`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockRes = mockk<Response<Unit>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST // anything but not created or conflict
        coEvery { accountServices.registerNewAccountAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `getUserProfileById should return failure when userServices getUserProfileAsync return status HTTP_OK`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockResBody = mockk<GetUserProfileResponse>(relaxed = true)
        val mockRes = mockk<Response<GetUserProfileResponse>>(relaxed = true)

        every { mockResBody.user } returns mockUserProfile
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockResBody
        coEvery { userServices.getUserProfileAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getUserProfileById(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(UserDomainModel.from(mockUserProfile))
    }

    @Test
    fun `getUserProfileById should return failure when userServices getUserProfileAsync return status other than HTTP_OK`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockResBody = mockk<GetUserProfileResponse>(relaxed = true)
        val mockRes = mockk<Response<GetUserProfileResponse>>(relaxed = true)

        every { mockResBody.user } returns mockUserProfile
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockResBody
        coEvery { userServices.getUserProfileAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getUserProfileById(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(UserDomainModel.from(mockUserProfile))
    }

    @Test
    fun `getUserProfileListByUserName should return correct result when userServices searchUserProfilesAsync return status HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockResBody = mockk<SearchUserProfilesResponse>(relaxed = true)
        val mockRes = mockk<Response<SearchUserProfilesResponse>>(relaxed = true)

        every { mockResBody.users } returns listOf(mockUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockResBody
        coEvery { userServices.searchUserProfilesAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getUserProfileListByUserName(mockUserNameKeyword)
        }

        // expect
        result shouldBeEqualTo Either.Success(listOf(mockUserProfile).map { UserDomainModel.from(it) })
    }

    @Test
    fun `getUserProfileListByUserName should return server error failure when userServices searchUserProfilesAsync return status other than HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockResBody = mockk<SearchUserProfilesResponse>(relaxed = true)
        val mockRes = mockk<Response<SearchUserProfilesResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockResBody
        coEvery { userServices.searchUserProfilesAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getUserProfileListByUserName(mockUserNameKeyword)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `userLogin should return correct result when accountServices loginAsync return with HTTP_OK`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockLoginResBody = mockk<LoginResponse>(relaxed = true)
        val mockLoginReq = mockk<Response<LoginResponse>>(relaxed = true)

        every { mockLoginResBody.loginCredential } returns mockLoginCredential
        every { mockLoginReq.code() } returns HttpURLConnection.HTTP_OK
        every { mockLoginReq.body() } returns mockLoginResBody

        val mockGetUserResBody = mockk<GetUserProfileResponse>(relaxed = true)
        val mockGetUserRes = mockk<Response<GetUserProfileResponse>>(relaxed = true)

        every { mockGetUserResBody.user } returns mockUserProfile
        every { mockGetUserRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockGetUserRes.body() } returns mockGetUserResBody

        coEvery { accountServices.loginAsync(any()) } returns mockLoginReq
        coEvery { userServices.getUserProfileAsync(any()) } returns mockGetUserRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userLogin(mockUserName, mockPassword)
        }

        // expect
        result shouldBeEqualTo Either.Success(UserDomainModel.from(mockUserProfile))
    }

    @Test
    fun `userLogin should return unauthorized failure result when accountServices loginAsync return with HTTP_UNAUTHORIZED`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockLoginResBody = mockk<LoginResponse>(relaxed = true)
        val mockLoginReq = mockk<Response<LoginResponse>>(relaxed = true)

        every { mockLoginReq.code() } returns HttpURLConnection.HTTP_UNAUTHORIZED
        every { mockLoginReq.body() } returns mockLoginResBody

        coEvery { accountServices.loginAsync(any()) } returns mockLoginReq

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userLogin(mockUserName, mockPassword)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LoginUserNameOrPasswordNotMatched)
    }

    @Test
    fun `userLogin should return server error failure result when accountServices loginAsync return other than HTTP_OK or HTTP_UNAUTHORIZED`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockLoginResBody = mockk<LoginResponse>(relaxed = true)
        val mockLoginReq = mockk<Response<LoginResponse>>(relaxed = true)

        every { mockLoginReq.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockLoginReq.body() } returns mockLoginResBody

        coEvery { accountServices.loginAsync(any()) } returns mockLoginReq

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userLogin(mockUserName, mockPassword)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `getPostByPostId should return correct result when postService getPost return HTTP_OK`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockReqBody = mockk<GetPostResponse>(relaxed = true)
        val mockReq = mockk<Response<GetPostResponse>>(relaxed = true)

        every { mockReqBody.post } returns mockPost
        every { mockReq.code() } returns HttpURLConnection.HTTP_OK
        every { mockReq.body() } returns mockReqBody

        coEvery { postServices.getPost(any()) } returns mockReq

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getPostByPostId(mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Success(PostDomainModel.from(mockPost))
    }

    @Test
    fun `getPostByPostId should return server error failure when postService getPost return other than HTTP_OK`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockReqBody = mockk<GetPostResponse>(relaxed = true)
        val mockReq = mockk<Response<GetPostResponse>>(relaxed = true)

        every { mockReq.code() } returns HttpURLConnection.HTTP_NOT_FOUND
        every { mockReq.body() } returns mockReqBody

        coEvery { postServices.getPost(any()) } returns mockReq

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getPostByPostId(mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `getPostListByUserId should return correct result when postServices getPosts return HTTP_OK`() {
        var result: Either<List<PostDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetPostsResponse>(relaxed = true)
        val mockReq = mockk<Response<GetPostsResponse>>(relaxed = true)

        every { mockReqBody.posts } returns listOf(mockPost)
        every { mockReq.code() } returns HttpURLConnection.HTTP_OK
        every { mockReq.body() } returns mockReqBody

        coEvery { postServices.getPosts(any()) } returns mockReq

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getPostListByUserId(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(listOf(mockPost).map { PostDomainModel.from(it) })
    }

    @Test
    fun `getPostListByUserId should return server error failure when postServices getPosts return other than HTTP_OK`() {
        var result: Either<List<PostDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetPostsResponse>(relaxed = true)
        val mockReq = mockk<Response<GetPostsResponse>>(relaxed = true)

        every { mockReq.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockReq.body() } returns mockReqBody

        coEvery { postServices.getPosts(any()) } returns mockReq

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getPostListByUserId(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

}