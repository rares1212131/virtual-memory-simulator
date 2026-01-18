import React from 'react';

function StatsPanel({ stats }) {
  return (
    <div className="card">
      <div className="card-header">Statistics</div>
      <div className="card-body">
        <div className="row">
          <div className="col-7">
            <p className="mb-1">TLB Hits:</p>
            <p className="mb-1">TLB Misses:</p>
            <p className="mb-1">Page Table Hits:</p>
            <p className="mb-1">Page Faults:</p>
          </div>
          <div className="col-5 text-end">
            <p className="mb-1"><strong>{stats.tlbHits}</strong></p>
            <p className="mb-1"><strong>{stats.tlbMisses}</strong></p>
            <p className="mb-1"><strong>{stats.pageTableHits}</strong></p>
            <p className="mb-1"><strong>{stats.pageFaults}</strong></p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StatsPanel;