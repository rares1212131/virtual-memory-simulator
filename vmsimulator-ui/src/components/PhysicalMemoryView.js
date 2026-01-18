
import React from 'react';

function PhysicalMemoryView({ physicalMemory }) {
  return (
    <div className="card">
      <div className="card-header">Physical Memory (RAM)</div>
      <div className="card-body memory-view">
        {physicalMemory.map((frame) => (
          <div key={frame.id} className="mb-2 border p-1 bg-light">
            <div className="d-flex justify-content-between align-items-center mb-1">
              <span className="badge bg-danger">PPN: 0x{frame.id.toString(16).toUpperCase()}</span>
              <span className="small text-muted">
                {frame.occupiedByVpn !== -1
                  ? `VPN: 0x${frame.occupiedByVpn.toString(16).toUpperCase()}`
                  : 'Free'}
              </span>
            </div>

            {/* The Byte Grid */}
            <div className="d-flex flex-wrap" style={{ gap: '2px' }}>
              {frame.data && frame.data.map((byteVal, index) => (
                <div
                  key={index}
                  style={{
                    width: '24px',
                    height: '24px',
                    fontSize: '10px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    border: '1px solid #ccc',
                    backgroundColor: '#fff',
                    fontFamily: 'monospace'
                  }}
                  title={`Offset: ${index.toString(16)}`} // Tooltip on hover
                >
                  {byteVal.toString(16).toUpperCase().padStart(2, '0')}
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default PhysicalMemoryView;