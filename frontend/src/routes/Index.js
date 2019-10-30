import React, { useState } from 'react';
import { Link, useHistory } from 'react-router-dom';
import { Colors, Classes, MenuItem } from "@blueprintjs/core";
import { IconNames } from '@blueprintjs/icons';
import { Suggest } from '@blueprintjs/select';

function Index() {
  const routerHistory = useHistory();
  const [query, setQuery] = useState('');
  const [searchResult, setSearchResult] = useState([]);

  async function search(query) {
    setQuery(query);
    const result = await fetch(`http://localhost:18002/media:search?q=${encodeURIComponent(query)}`)
      .then(r => r.json());
    setSearchResult(result);
  }

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
        <Suggest
          inputProps={{
            type: 'search',
            large: true,
            leftIcon: IconNames.SEARCH,
            placeholder: "Search by titles",
          }}
          fill={true}
          query={query}
          onQueryChange={search}
          items={searchResult}
          initialContent={null}
          itemRenderer={(media, { modifiers }) => {
              if (!modifiers.matchesPredicate) {
                return null;
              }
              return (
                <MenuItem
                  active={modifiers.active}
                  disabled={modifiers.disabled}
                  key={media.id}
                  text={media.name}
                  href={`/media/${media.id}`}
                  style={{ width: '630px' }}
                />
              );
          }}
          onItemSelect={media => routerHistory.push(`/media/${media.id}`)}
          popoverProps={{ minimal: true }}
          inputValueRenderer={media => media.name}
        />
      </div>
    </div>
  )
}

export default Index;
