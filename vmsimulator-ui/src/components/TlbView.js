import React from 'react';

const renderBits = (pte) => {
  if (!pte) return <small className="text-muted">----</small>;
  return (
    <span className="font-monospace small">
      {pte.validBit ? '1' : '0'}{' '}
      {pte.dirtyBit ? '1' : '0'}{' '}
      {pte.referenceBit ? '1' : '0'}{' '}
      {pte.writeBit ? '1' : '0'}
    </span>
  );
};

function TlbView({ tlb }) {
  return (
    <div className="card">
      <div className="card-header">Translation Lookaside Buffer (TLB)</div>
      <div className="card-body memory-view">
        {tlb.map((set) => (
          <div key={set.index} className="mb-2">
            <strong>Set {set.index}</strong>
            <table className="table table-sm table-bordered">
              <thead>
                <tr>
                  <th>VPN</th>
                  <th>Tag</th>
                  <th>PPN</th>
                  <th>Bits (VDRW)</th>
                </tr>
              </thead>
              <tbody>
                {set.entries.map((entry, idx) => (
                  <tr key={idx}>
                    <td>0x{entry.vpn.toString(16)}</td>
                    <td>0x{entry.tag.toString(16)}</td>
                    <td>0x{entry.physicalPageNumber.toString(16)}</td>
                    <td>{renderBits(entry.pageTableEntry)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ))}
      </div>
    </div>
  );
}

export default TlbView;