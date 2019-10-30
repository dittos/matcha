import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import { MenuItem } from "@blueprintjs/core";
import { IconNames } from '@blueprintjs/icons';
import { Suggest } from '@blueprintjs/select';

export function SearchBar({ muted = false }) {
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
    <Suggest
      inputProps={{
        type: 'search',
        large: true,
        leftIcon: IconNames.SEARCH,
        placeholder: "Search by titles",
        style: muted ? {
          background: 'transparent',
          color: '#fff'
        } : {}
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
  );
}
