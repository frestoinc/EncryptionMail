package com.frestoinc.maildemo.ui.sharedviewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;

import com.frestoinc.maildemo.base.rx.SchedulerProvider;
import com.frestoinc.maildemo.data.local.DataManager;
import com.frestoinc.maildemo.data.model.AccountUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by frestoinc on 30,January,2020 for MailDemo.
 */
@RunWith(JUnit4.class)
public class AccountViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Inject
    SchedulerProvider schedulerProvider;
    @Inject
    DataManager manager;
    @Mock
    Observer<List<AccountUser>> allAccountsObserver;
    @Mock
    LifecycleOwner lifecycleOwner;
    Lifecycle lifecycle;
    private AccountViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        lifecycle = new LifecycleRegistry(lifecycleOwner);
        viewModel = new AccountViewModel(schedulerProvider, manager);
        viewModel.getAccountUsers().observeForever(allAccountsObserver);
    }

    @After
    public void tearDown() throws Exception {
        viewModel = null;
    }

    @Test
    public void testNull() {
        when(manager.getAllAccounts()).thenReturn(null);
        assertNotNull(viewModel.getAccountUsers());
        assertTrue(viewModel.getAccountUsers().hasObservers());
    }

    @Test
    public void getAccountUsers() {
        List<AccountUser> list = new ArrayList<>();
        when(manager.getAllAccounts()).thenReturn(Single.just(list));
        viewModel.getAccountUsers();
        verify(allAccountsObserver).onChanged(list);
        list.add(new AccountUser("abc@123.com", "abc", "server"));
        list.add(new AccountUser("123@abc.def", "123", "server"));
        verify(allAccountsObserver).onChanged(list);
    }

    @Test
    public void setAccountUser() {
    }

    @Test
    public void getRoomAccountUsers() {
    }

    @Test
    public void removeRoomAccountUsers() {
    }

    @Test
    public void setSessionAccountUser() {
    }
}