package com.example.instagram_clone_clean_architecture.app.data.data_source

import com.example.instagram_clone_clean_architecture.app.data.model.LoginCredentialDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.PostDataModel
import com.example.instagram_clone_clean_architecture.app.data.model.UserProfileDataModel
import com.example.instagram_clone_clean_architecture.app.data.retrofit.responses.*
import com.example.instagram_clone_clean_architecture.app.data.retrofit.services.*
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.*
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.util.*

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

    @MockK(relaxed = true)
    internal lateinit var relationServices: RelationServices

    @MockK(relaxed = true)
    internal lateinit var likeServices: LikeServices

    private lateinit var remoteDataSourceImpl: RemoteDataSource

    private val mockUserId = "mockUserId"
    private val mockUserName = "mockUserName"
    private val mockPassword = "mockPassword"
    private val mockUserNameKeyword = "mocKeyword"

    private val mockSecondUserId = "mockSecUserId"
    private val mockSecondUserName = "mockSecondUserName"

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

    private val mockUploadUserProfile = UserProfileUploadDomainModel(
        id = mockUserId,
        userName = mockUserName,
        name = "mockAlias",
        description = "mockDes",
        imageUri = mockk(relaxed = true)
    )

    private val mockSecondUserProfile = UserProfileDataModel(
        id = mockSecondUserId,
        userName = mockSecondUserName,
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

    private val mockUploadPost = PostUploadDomainModel(
        date = Date(),
        belongUserId = mockUserId,
        cachedImageFile = mockk(relaxed = true)
    )

    private val mockLoginCredential = LoginCredentialDataModel(
        jwt = "mockJwt",
        userId = mockUserId
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        remoteDataSourceImpl = RemoteDataSourceImpl(accountServices, userServices, postServices, relationServices, likeServices)
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
    fun `userRegister should return network connection failure when accountService getUserProfileAsync throw SocketTimeoutException`() {
        var result: Either<Unit, Failure>? = null

        // given
        coEvery { accountServices.registerNewAccountAsync(any()) } throws SocketTimeoutException()

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
        }

        // expect
        result shouldNotBeEqualTo null
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

    @Test
    fun `userRegister should throw exception when accountService getUserProfileAsync throw exception that is not SocketTimeoutException`() {
        var result: Either<Unit, Failure>? = null
        var exception: Exception? = null

        // given
        coEvery { accountServices.registerNewAccountAsync(any()) } throws IllegalStateException()

        // when
        mainCoroutineRule.runBlockingTest {
            try {
                result = remoteDataSourceImpl.userRegister(mockUserName, mockPassword)
            } catch (e: Exception) {
                exception = e
            }
        }

        // expect
        result shouldBeEqualTo null
        exception shouldBeInstanceOf IllegalStateException::class.java
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
        var result: Either<LoginCredentialDomainModel, Failure>? = null

        // given
        val mockLoginResBody = mockk<LoginResponse>(relaxed = true)
        val mockLoginReq = mockk<Response<LoginResponse>>(relaxed = true)

        every { mockLoginResBody.credential } returns mockLoginCredential
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
        result shouldBeEqualTo Either.Success(
            LoginCredentialDomainModel(
            mockLoginCredential.jwt,
            UserDomainModel.from(mockUserProfile)
        )
        )
    }

    @Test
    fun `userLogin should return unauthorized failure result when accountServices loginAsync return with HTTP_UNAUTHORIZED`() {
        var result: Either<LoginCredentialDomainModel, Failure>? = null

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
        var result: Either<LoginCredentialDomainModel, Failure>? = null

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
        val mockRes = mockk<Response<GetPostResponse>>(relaxed = true)

        every { mockReqBody.post } returns mockPost
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { postServices.getPostAsync(any()) } returns mockRes

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
        val mockRes = mockk<Response<GetPostResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_NOT_FOUND
        every { mockRes.body() } returns mockReqBody

        coEvery { postServices.getPostAsync(any()) } returns mockRes

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
        val mockRes = mockk<Response<GetPostsResponse>>(relaxed = true)

        every { mockReqBody.posts } returns listOf(mockPost)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { postServices.getPostsAsync(any()) } returns mockRes

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
        val mockRes = mockk<Response<GetPostsResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockReqBody

        coEvery { postServices.getPostsAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getPostListByUserId(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `getFollowingUsersById should return correct result when relationService getFollowings return with HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetFollowingsResponse>(relaxed = true)
        val mockRes = mockk<Response<GetFollowingsResponse>>(relaxed = true)

        every { mockReqBody.followings } returns listOf(mockSecondUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.getFollowingsAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getFollowingUsersById(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(listOf(mockSecondUserProfile).map { UserDomainModel.from(it) })
    }

    @Test
    fun `getFollowingUsersById should return server error failure when relationService getFollowings return other than HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetFollowingsResponse>(relaxed = true)
        val mockRes = mockk<Response<GetFollowingsResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.getFollowingsAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getFollowingUsersById(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `getFollowerUsersById should return correct result when relationService getFollowers return with HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetFollowersResponse>(relaxed = true)
        val mockRes = mockk<Response<GetFollowersResponse>>(relaxed = true)

        every { mockReqBody.followers } returns listOf(mockSecondUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.getFollowersAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getFollowerUsersById(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(listOf(mockSecondUserProfile).map { UserDomainModel.from(it) })
    }

    @Test
    fun `getFollowerUsersById should return server error failure when relationService getFollowers return other than HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetFollowersResponse>(relaxed = true)
        val mockRes = mockk<Response<GetFollowersResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.getFollowersAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getFollowerUsersById(mockUserId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `addUserRelation should return correct result when relationService addRelation return with HTTP_CREATED`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<AddRelationResponse>(relaxed = true)
        val mockRes = mockk<Response<AddRelationResponse>>(relaxed = true)

        every { mockReqBody.followings } returns listOf(mockSecondUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_CREATED
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.addRelationAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.addUserRelation(mockUserId, mockSecondUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `addUserRelation should return server error failure when relationService addRelation return other than HTTP_CREATED`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<AddRelationResponse>(relaxed = true)
        val mockRes = mockk<Response<AddRelationResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_UNAUTHORIZED
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.addRelationAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.addUserRelation(mockUserId, mockSecondUserId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `removeUserRelation should return correct result when relationService removeRelation return with HTTP_OK`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<RemoveRelationResponse>(relaxed = true)
        val mockRes = mockk<Response<RemoveRelationResponse>>(relaxed = true)

        every { mockReqBody.followings } returns listOf(mockSecondUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.removeRelationAsync(any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.removeUserRelation(mockUserId, mockSecondUserId)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `removeUserRelation should return server error failure when relationService removeRelation return other than HTTP_OK`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<RemoveRelationResponse>(relaxed = true)
        val mockRes = mockk<Response<RemoveRelationResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_UNAUTHORIZED
        every { mockRes.body() } returns mockReqBody

        coEvery { relationServices.removeRelationAsync(any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.removeUserRelation(mockUserId, mockSecondUserId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `getLikedUsersByPostId should return correct result when likeService getLikes return with HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetLikesResponse>(relaxed = true)
        val mockRes = mockk<Response<GetLikesResponse>>(relaxed = true)

        every { mockReqBody.likedUsers } returns listOf(mockUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { likeServices.getLikesAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getLikedUsersByPostId(mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Success(listOf(mockUserProfile).map { UserDomainModel.from(it) })
    }

    @Test
    fun `getLikedUsersByPostId should return server error failure when likeService getLikes return other than HTTP_OK`() {
        var result: Either<List<UserDomainModel>, Failure>? = null

        // given
        val mockReqBody = mockk<GetLikesResponse>(relaxed = true)
        val mockRes = mockk<Response<GetLikesResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockReqBody

        coEvery { likeServices.getLikesAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.getLikedUsersByPostId(mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `addUserLikePost should return correct result when likeService likePost return with HTTP_CREATED`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<LikePostResponse>(relaxed = true)
        val mockRes = mockk<Response<LikePostResponse>>(relaxed = true)

        every { mockReqBody.likedUsers } returns listOf(mockUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_CREATED
        every { mockRes.body() } returns mockReqBody

        coEvery { likeServices.likePostAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.addUserLikePost(mockUserId, mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `addUserLikePost should return server error failure when likeService likePost return other than HTTP_CREATED`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<LikePostResponse>(relaxed = true)
        val mockRes = mockk<Response<LikePostResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockReqBody

        coEvery { likeServices.likePostAsync(any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.addUserLikePost(mockUserId, mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `removeUserLikePost should return correct result when likeService dislikePost return with HTTP_OK`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockReqBody = mockk<DislikePostResponse>(relaxed = true)
        val mockRes = mockk<Response<DislikePostResponse>>(relaxed = true)

        every { mockReqBody.likedUsers } returns listOf(mockUserProfile)
        every { mockRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockRes.body() } returns mockReqBody

        coEvery { likeServices.dislikePostAsync(any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.removeUserLikePost(mockUserId, mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `removeUserLikePost should return server error failure when likeService dislikePost return other than HTTP_OK`() {
        var result: Either<Unit, Failure>? = null

        // given
        val mockResBody = mockk<DislikePostResponse>(relaxed = true)
        val mockRes = mockk<Response<DislikePostResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_BAD_REQUEST
        every { mockRes.body() } returns mockResBody

        coEvery { likeServices.dislikePostAsync(any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.removeUserLikePost(mockUserId, mockPostId)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `updateUserProfile should return correct result when userService updateUserProfile return with HTTP_CREATED`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockResBody = mockk<UpdateUserProfileResponse>(relaxed = true)
        val mockRes = mockk<Response<UpdateUserProfileResponse>>(relaxed = true)

        every { mockResBody.userId } returns mockUserId
        every { mockRes.code() } returns HttpURLConnection.HTTP_CREATED
        every { mockRes.body() } returns mockResBody

        val mockGetResBody = mockk<GetUserProfileResponse>(relaxed = true)
        val mockGetRes = mockk<Response<GetUserProfileResponse>>(relaxed = true)

        every { mockGetResBody.user } returns mockUserProfile
        every { mockGetRes.code() } returns HttpURLConnection.HTTP_OK
        every { mockGetRes.body() } returns mockGetResBody

        coEvery { userServices.updateUserProfileAsync(any(), any(), any(), any(), any(), any()) } returns mockRes
        coEvery { userServices.getUserProfileAsync(any()) } returns mockGetRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.updateUserProfile(mockUploadUserProfile)
        }

        // expect
        result shouldBeEqualTo Either.Success(UserDomainModel.from(mockUserProfile))
    }

    @Test
    fun `updateUserProfile should return server error failure when userService updateUserProfile return other than HTTP_CREATED`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val mockRes = mockk<Response<UpdateUserProfileResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_UNAUTHORIZED
        every { mockRes.body() } returns null

        coEvery { userServices.updateUserProfileAsync(any(), any(), any(), any(), any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.updateUserProfile(mockUploadUserProfile)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

    @Test
    fun `uploadPost should return post not complete failure when given post is not upload ready`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockFailUploadPost = mockk<PostUploadDomainModel>(relaxed = true)

        every { mockFailUploadPost.isPostUploadReady } returns false

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.uploadPost(mockFailUploadPost)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.PostNotComplete)
    }

    @Test
    fun `uploadPost should return correct result when postServices addNewPost return with HTTP_CREATED`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockResBody = mockk<AddNewPostResponse>(relaxed = true)
        val mockRes = mockk<Response<AddNewPostResponse>>(relaxed = true)

        every { mockResBody.post } returns mockPost
        every { mockRes.code() } returns HttpURLConnection.HTTP_CREATED
        every { mockRes.body() } returns mockResBody

        coEvery { postServices.addNewPostAsync(any(), any(), any(), any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.uploadPost(mockUploadPost)
        }

        // expect
        result shouldBeEqualTo Either.Success(PostDomainModel.from(mockPost))
    }

    @Test
    fun `uploadPost should return server error failure when postServices addNewPost return other than HTTP_CREATED`() {
        var result: Either<PostDomainModel, Failure>? = null

        // given
        val mockRes = mockk<Response<AddNewPostResponse>>(relaxed = true)

        every { mockRes.code() } returns HttpURLConnection.HTTP_UNAUTHORIZED
        every { mockRes.body() } returns null

        coEvery { postServices.addNewPostAsync(any(), any(), any(), any(), any()) } returns mockRes

        // when
        mainCoroutineRule.runBlockingTest {
            result = remoteDataSourceImpl.uploadPost(mockUploadPost)
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ServerError)
    }

}