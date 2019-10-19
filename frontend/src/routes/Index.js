import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

function Index() {
  const [query, setQuery] = useState('');
  const [searchResult, setSearchResult] = useState([]);

  async function search(event) {
    event.preventDefault();
    const result = await fetch(`http://localhost:18002/media:search?q=${encodeURIComponent(query)}`)
      .then(r => r.json());
    setSearchResult(result);
  }

  return (
    <div>
      <form onSubmit={search}>
        <p>Search: <input value={query} onChange={event => setQuery(event.target.value)} /></p>
      </form>
      
      {searchResult.map(media => (
        <Link to={`/media/${media.id}`} style={{ display: 'flex', paddingBottom: '10px' }}>
          {media.image &&
            <img src={media.image}
                style={{ width: '80px', height: 'auto', marginRight: '10px' }} />}
          <div>
            <b>{media.name}</b>
          </div>
        </Link>
      ))}
    </div>
  )
}

export default Index;
