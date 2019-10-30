import React from 'react';
import { Colors, Classes } from "@blueprintjs/core";
import { SearchBar } from '../components/SearchBar';

function Index() {
  return (
    <div style={{ background: Colors.LIGHT_GRAY4, height: '100vh' }}>
      <div style={{
        background: Colors.LIME1,
        height: '50vh',
        display: 'flex',
        alignItems: 'center',
        justifyItems: 'center',
      }}>
        <h1 style={{
          margin: '0 auto',
          textTransform: 'uppercase',
          color: Colors.WHITE,
          fontSize: '48px',
        }}>
          Matcha
        </h1>
      </div>

      <div 
        className={Classes.ELEVATION_4}
        style={{ borderRadius: '30px', margin: '-20px auto 0', width: '640px' }}
      >
        <SearchBar />
      </div>
    </div>
  )
}

export default Index;
