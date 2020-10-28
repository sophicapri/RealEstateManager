package com.sophieoc.realestatemanager.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.sophieoc.realestatemanager.model.User
import com.sophieoc.realestatemanager.model.UserWithProperties
import com.sophieoc.realestatemanager.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserViewModelTest {
    private lateinit var viewModel: UserViewModel
    private lateinit var userSource: UserRepository
    private val user = User("42", "Dummy user", "", "")
    private var userWithProperties = UserWithProperties(user, ArrayList())
    private val userMutable = MutableLiveData(mockk<UserWithProperties>())

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        userSource = mockk()
        every { userSource.currentUser } returns userMutable
        viewModel = UserViewModel(userSource)
    }

    @Test
    fun `get user by id with success`() {
        userMutable.value = userWithProperties
        every { userSource.getUserWithProperties(any()) } returns userMutable
        viewModel.getUserById(String()).observeForever {
            assertSame(userMutable.value, it)
            assertSame(userMutable.value?.user?.uid, it.user.uid)
        }
    }

    @Test
    fun `get current user with success`() {
        userMutable.value = userWithProperties
        viewModel.currentUser.observeForever {
            assertSame(userMutable.value, it)
            assertSame(userMutable.value?.user?.uid, it.user.uid)
        }
    }

    @Test
    fun `update user with success`() {
        val userMutable = MutableLiveData(mockk<UserWithProperties>())
        userMutable.value = userWithProperties
        every { userSource.upsertUser(any()) } returns userMutable
        viewModel.userUpdated.observeForever {
            assertSame(userMutable.value, it)
            assertSame(userMutable.value?.user?.username, it.user.username)
        }
    }
}