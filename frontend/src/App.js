import React, { useState, useEffect } from 'react';
import * as firebase from "firebase/app";
import * as firebaseui from "firebaseui";
import './App.css';
import Collection from './Collection';

var ui;

function login() {
  if (!ui) {
    ui = new firebaseui.auth.AuthUI(firebase.auth());
  }
  ui.start('#firebaseui-auth-container', {
    callbacks: {
      signInSuccessWithAuthResult: function(authResult, redirectUrl) {
        // User successfully signed in.
        // Return type determines whether we continue the redirect automatically
        // or whether we leave that to developer to handle.
        return false;
      },
    },
    signInOptions: [
      firebase.auth.EmailAuthProvider.PROVIDER_ID
    ],
    // Other config options...
  });
}

function logout() {
  firebase.auth().signOut();
}

function App() {
  const [user, setUser] = useState('loading');

  useEffect(() => {
    const unsubscribe = firebase.auth().onAuthStateChanged(function(user) {
      setUser(user);
    });
    return unsubscribe;
  }, []);

  if (user === 'loading') {
    return 'Loading...';
  }

  return (
    <div className="App">
      {user ? (
        <div>
          Welcome! {user.email}
          <button onClick={logout}>Logout</button>
        </div>
      ) : (
        <button onClick={login}>Login</button>
      )}

      {user &&
        <Collection user={user} />}
    </div>
  );
}

export default App;
