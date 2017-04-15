package com.chuyeu.training.myapp.services.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.chuyeu.training.myapp.dao.api.IUserCredentialsDao;
import com.chuyeu.training.myapp.dao.api.IUserProfileDao;
import com.chuyeu.training.myapp.datamodel.UserCredentials;
import com.chuyeu.training.myapp.datamodel.UserProfile;
import com.chuyeu.training.myapp.services.IUserService;

@Service
public class UserSeviceImpl implements IUserService {

	@Inject
	private IUserProfileDao userProfileDao;

	@Inject
	private IUserCredentialsDao userCredentialsDao;

	@Override
	public UserCredentials findUserCredentials(String email, String password) {
		return userCredentialsDao.find(email, password);
	}

	@Override
	public UserCredentials getCredentials(Integer id) {
		return userCredentialsDao.get(id);
	}

	@Override
	public void update(UserCredentials credentials) {
		userCredentialsDao.update(credentials);

	}

	@Override
	public UserProfile saveUser(UserProfile userProfile, UserCredentials userCredentials) throws DuplicateKeyException {

		Integer userCredentialsId = userCredentialsDao.add(userCredentials);
		userProfile.setUserCredentialsId(userCredentialsId);
		return userProfileDao.insert(userProfile);
	}
	

	@Override
	public UserProfile getUserProfile(Integer id) {
		return userProfileDao.get(id);
	}

	@Override
	public void update(UserProfile profile) {
		userProfileDao.update(profile);

	}

	@Override
	public List<UserProfile> getAll() {
		return userProfileDao.getAll();
	}
	
	@Override
	public void delete(Integer id) {
		userProfileDao.delete(id);
		userCredentialsDao.delete(id);
	}

}
