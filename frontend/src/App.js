import React, { useState, useEffect } from 'react';
import * as firebase from "firebase/app";
import * as firebaseui from "firebaseui";
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import './App.css';
import Index from './routes/Index';
import Media from './routes/Media';
import Collection from './routes/Collection';

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
    <BrowserRouter>
      <div style={{ position: 'absolute', right: 0, padding: '20px' }}>
        {user ? (
          <div>
            Welcome! {user.email}
            <button onClick={logout}>Logout</button>
          </div>
        ) : (
          <button onClick={login}>Login</button>
        )}
      </div>

      <Switch>
        <Route path="/media/:id">
          <Media user={user} />
        </Route>
        <Route path="/collection">
          {user &&
            <Collection user={user} />}
        </Route>
        <Route path="/">
          <Index />
        </Route>
      </Switch>
    </BrowserRouter>
  );
}

export default App;
