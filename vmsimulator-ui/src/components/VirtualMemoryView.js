import React from 'react';

function VirtualMemoryView({ disk, onAllocatePage }) {
  return (
    <div className="card">
      <div className="card-header">Virtual Memory / Disk</div>
      <div className="card-body memory-view">
        {disk.map((diskPage) => (
          <div key={diskPage.vpn} className="d-flex align-items-center border-bottom p-1">
            <div className="fw-bold">
              VPN: 0x{diskPage.vpn.toString(16).padStart(2, '0')}
            </div>
            <div className="ms-auto">
              {diskPage.status === 'Unallocated' ? (
                <button
                  className="btn btn-outline-success btn-sm"
                  onClick={() => onAllocatePage(diskPage.vpn)}
                >
                  Allocate
                </button>
              ) : (
                <span className="badge bg-secondary">{diskPage.status}</span>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default VirtualMemoryView;