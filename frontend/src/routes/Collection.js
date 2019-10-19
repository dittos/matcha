import React, { useState, useEffect } from 'react';

function Collection({ user }) {
  const [items, setItems] = useState([]);

  useEffect(() => {
    const fire = async () => {
      if (user) {
        const accessToken = await user.getIdToken();
        const items = await fetch(`http://localhost:18001/users/${user.uid}/items`, {
          mode: 'cors',
          headers: {
            'Authorization': 'Bearer ' + accessToken
          }
        }).then(r => r.json());
        setItems(items);
      } else {
        setItems([]);
      }
    }
    fire();
  }, [user]);

  return (
    <div>
      {items.map(item => (
        <div>
          {JSON.stringify(item)}
        </div>
      ))}
    </div>
  )
}

export default Collection;
