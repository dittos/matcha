import React, { useState, useEffect, useRef } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Blurhash } from 'react-blurhash';
import { Card } from '@blueprintjs/core';
import { SearchBar } from '../components/SearchBar';

function Media({ user }) {
  const { id } = useParams();
  const [media, setMedia] = useState(null);
  const [item, setItem] = useState(null);
  const ratingEl = useRef(null);
  const commentEl = useRef(null);

  useEffect(() => {
    async function load() {
      const accessToken = user ? await user.getIdToken() : null;
      const { media, item } = await fetch(`http://localhost:18000/media/${id}`, {
        mode: 'cors',
        headers: accessToken ? {
          'Authorization': 'Bearer ' + accessToken
        } : {},
      }).then(r => r.json());
      setMedia(media);
      setItem(item);
    }
    load();
  }, [id, user]);

  if (!media) {
    return 'Loading...';
  }

  async function createItem() {
    const request = {
      userId: user.uid,
      mediaId: id,
      rating: ratingEl.current.value,
      comment: commentEl.current.value,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    };
    const item = await user.getIdToken().then(accessToken =>
      fetch(`http://localhost:18001/users/${user.uid}/items`, {
        method: 'POST',
        body: JSON.stringify(request),
        mode: 'cors',
        headers: {
          'Authorization': 'Bearer ' + accessToken,
          'Content-Type': 'application/json',
        }
      })
    ).then(r => r.json());
    setItem(item);
  }

  return (
    <div>
      <div style={{ position: 'relative' }}>
        {media.imageBlurhash &&
          <Blurhash hash={media.imageBlurhash} width="100%" height="200px" punch={1} />}
        <div style={{ position: 'absolute', left: 0, top: 0, bottom: 0, right: 0 }}>
          <h1 style={{
            margin: '0 auto',
            textTransform: 'uppercase',
            color: '#fff',
            fontSize: '24px',
            position: 'absolute',
            left: '20px',
            top: '13px',
          }}>
            <Link to="/" style={{color: '#fff'}}>Matcha</Link>
          </h1>
          <div style={{ margin: '10px auto', width: '640px' }}>
            <SearchBar muted={true} />
          </div>
        </div>
      </div>
      <div style={{ display: 'flex', width: '640px', margin: '-20px auto 0', position: 'relative' }}>
        {media.image &&
          <div className="bp3-elevation-1" style={{ padding: '2px', background: '#fff', marginRight: '20px' }}>
            <img src={media.image}
                style={{ width: '150px', height: 'auto', display: 'block' }} />
          </div>}
        <div style={{ marginTop: '30px' }}>
          <h2 className="bp3-heading">{media.name}</h2>
          {item ? (
            <div>
              Added | {'★'.repeat(item.rating)}
            </div>
          ) : (
            <div>
              <p>
                Rating:{' '}
                <select ref={ratingEl}>
                  {[1, 2, 3, 4, 5].map(rating => (
                    <option value={rating}>{'★'.repeat(rating)}</option>
                  ))}
                </select>
              </p>
              <p>
                Comment:{' '}
                <input type="text" ref={commentEl} />
              </p>
              <button onClick={createItem}>Add</button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}

export default Media;
