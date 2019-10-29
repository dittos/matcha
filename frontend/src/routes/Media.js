import React, { useState, useEffect, useRef } from 'react';
import { Link, useParams } from 'react-router-dom';

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
    <div style={{ display: 'flex' }}>
      {media.image &&
        <img src={media.image}
            style={{ width: '80px', height: 'auto', marginRight: '10px' }} />}
      <div>
        <b>{media.name}</b>
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
  )
}

export default Media;
