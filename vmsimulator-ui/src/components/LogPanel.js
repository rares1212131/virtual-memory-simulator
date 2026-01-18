import React, { useEffect, useRef } from 'react';

function LogPanel({ messages }) {
  // This ref will now point to the scrollable div itself
  const scrollableDivRef = useRef(null);

  // This effect will run whenever the 'messages' array changes
  useEffect(() => {
    const scrollableDiv = scrollableDivRef.current;
    if (scrollableDiv) {
      scrollableDiv.scrollTop = scrollableDiv.scrollHeight;
    }
  }, [messages]); 

  return (
    <div className="card mt-3">
      <div className="card-header">Simulation Messages</div>
      {/* Add the ref to the div that has the scrollbar */}
      <div className="card-body log-panel" ref={scrollableDivRef}>
        {messages.map((msg, index) => (
          <p key={index} className="mb-1 font-monospace small">
            {msg}
          </p>
        ))}
        {/* We no longer need the empty div at the bottom */}
      </div>
    </div>
  );
}

export default LogPanel;