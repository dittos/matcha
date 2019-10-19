import React from 'react';
import ReactDOM from 'react-dom';
import * as firebase from "firebase/app";
import "firebase/auth";
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

// Your web app's Firebase configuration
var firebaseConfig = {
  apiKey: "AIzaSyDUal6JxCDopkQ_Ds2UgD3uVseTJNE7aZw",
  authDomain: "animeta-fad71.firebaseapp.com",
  databaseURL: "https://animeta-fad71.firebaseio.com",
  projectId: "animeta-fad71",
  storageBucket: "animeta-fad71.appspot.com",
  messagingSenderId: "480664025755",
  appId: "1:480664025755:web:1474fc02fe69f32f26e43e"
};
// Initialize Firebase
firebase.initializeApp(firebaseConfig);

ReactDOM.render(<App />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
