import React from 'react';

function ConfigPanel({ config, derivedValues }) {
  if (!config || !derivedValues) return null;

  return (
    <div className="card mb-3">
      <div className="card-header bg-secondary text-white">
        System Architecture
      </div>
      <div className="card-body small">
        {/* Top Section: Basic Hardware Params */}
        <div className="row mb-2">
          <div className="col-6">
            <strong>Addr Width:</strong><br/> {config.virtualAddressWidth} bits
          </div>
          <div className="col-6">
             <strong>Page Size:</strong><br/> {config.pageSize} Bytes
          </div>
        </div>
        <div className="row mb-2">
           <div className="col-6">
             <strong>RAM Size:</strong><br/> {config.physicalMemorySize} Bytes
           </div>
           <div className="col-6">
             <strong>Algorithm:</strong><br/> <span className="badge bg-info text-dark">{config.algorithm}</span>
           </div>
        </div>

        <hr className="my-2"/>
        
        {/* Middle Section: TLB Specs */}
        <div className="mb-2">
          <strong>TLB:</strong> {config.tlbSize} Entries, {config.tlbAssociativity}-Way
        </div>

        <hr className="my-2"/>

        {/* Bottom Section: The most important part for translation */}
        <h6 className="text-primary fw-bold">Address Translation Bits</h6>
        <div className="d-flex align-items-center justify-content-center border rounded bg-light p-2">
           <div className="text-center px-2 border-end border-dark">
              <span className="d-block fw-bold">{derivedValues.vpnBits} bits</span>
              <span className="text-muted" style={{fontSize: '0.8em'}}>VPN</span>
           </div>
           <div className="text-center px-2">
              <span className="d-block fw-bold">{derivedValues.offsetBits} bits</span>
              <span className="text-muted" style={{fontSize: '0.8em'}}>Offset</span>
           </div>
        </div>
        <div className="mt-1 text-center text-muted fst-italic" style={{fontSize: '0.75rem'}}>
           Hierarchy: {derivedValues.pdiBits} (L1) / {derivedValues.ptiBits} (L2)
        </div>

      </div>
    </div>
  );
}

export default ConfigPanel;