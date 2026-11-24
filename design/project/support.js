// DOC-08: minimal offline runtime so design/project/Main.dc.html opens in a browser and design/screens/render.sh can screenshot it.
(function () {
  class DCLogic {
    constructor(props) { this.props = props || {}; this.state = {}; }
    setState(update) {
      const part = typeof update === 'function' ? update(this.state) : update;
      this.state = Object.assign({}, this.state, part || {});
      schedule();
    }
    forceUpdate() { schedule(); }
  }
  window.DCLogic = DCLogic;

  let comp = null, tpl = null, host = null, pending = false;
  const params = new URLSearchParams(location.hash.slice(1));

  function lookup(expr, scope) {
    expr = expr.trim();
    if (expr === 'true') return true;
    if (expr === 'false') return false;
    if (expr === 'null') return null;
    if (/^-?\d+(\.\d+)?$/.test(expr)) return Number(expr);
    if (/^'.*'$/.test(expr)) return expr.slice(1, -1);
    let cur = scope;
    for (const part of expr.split('.')) {
      if (cur == null) return undefined;
      cur = cur[part];
    }
    return cur;
  }
  function interpolate(text, scope) {
    return text.replace(/\{\{([^}]*)\}\}/g, (m, e) => {
      const v = lookup(e, scope);
      return v == null || v === false ? '' : String(v);
    });
  }
  function single(text) {
    const m = /^\s*\{\{([^}]*)\}\}\s*$/.exec(text);
    return m ? m[1] : null;
  }
  const EVENTS = { onclick: 'click', onchange: 'input', onpointerup: 'pointerup', onpointerdown: 'pointerdown',
    onpointermove: 'pointermove', onpointercancel: 'pointercancel', oninput: 'input', onkeydown: 'keydown' };

  function renderChildren(src, parent, scope) {
    for (const child of Array.from(src.childNodes)) renderNode(child, parent, scope);
  }
  function renderNode(node, parent, scope) {
    if (node.nodeType === 3) { parent.appendChild(document.createTextNode(interpolate(node.nodeValue, scope))); return; }
    if (node.nodeType !== 1) return;
    const tag = node.localName;
    if (tag === 'script') return;
    if (tag === 'sc-if') {
      const expr = single(node.getAttribute('value') || '');
      if (expr != null && lookup(expr, scope)) renderChildren(node, parent, scope);
      return;
    }
    if (tag === 'sc-for') {
      const list = lookup(single(node.getAttribute('list') || '') || '', scope) || [];
      const name = node.getAttribute('as') || 'item';
      list.forEach((item, idx) => {
        const inner = Object.create(scope); inner[name] = item; inner.$index = idx;
        renderChildren(node, parent, inner);
      });
      return;
    }
    if (tag === 'helmet' || tag === 'x-dc') { renderChildren(node, parent, scope); return; }
    const el = node.cloneNode(false);
    for (const attr of Array.from(node.attributes)) {
      if (attr.name.startsWith('hint-')) { el.removeAttribute(attr.name); continue; }
      if (!attr.value.includes('{{')) continue;
      const key = attr.name.toLowerCase();
      const expr = single(attr.value);
      if (EVENTS[key]) {
        el.removeAttribute(attr.name);
        const fn = expr ? lookup(expr, scope) : null;
        if (typeof fn === 'function') el.addEventListener(EVENTS[key], (ev) => fn(ev));
        continue;
      }
      const val = expr != null ? lookup(expr, scope) : interpolate(attr.value, scope);
      if (val === false || val == null) { el.removeAttribute(attr.name); continue; }
      if (key === 'value' && 'value' in el) el.value = String(val);
      el.setAttribute(attr.name, String(val));
    }
    if (tag === 'template') { parent.appendChild(el); return; }
    renderChildren(node, el, scope);
    parent.appendChild(el);
  }
  function render() {
    pending = false;
    const vals = comp.renderVals();
    const frag = document.createDocumentFragment();
    renderChildren(tpl, frag, vals);
    host.replaceChildren(frag);
  }
  function schedule() { if (!pending && comp) { pending = true; setTimeout(render, 0); } }

  function clickByText(label) {
    const all = Array.from(host.querySelectorAll('button,[role=button],[aria-label],a,label,div,span'));
    const norm = (s) => (s || '').replace(/\s+/g, ' ').trim();
    let hit = all.find((e) => norm(e.getAttribute && e.getAttribute('aria-label')) === label);
    if (!hit) hit = all.filter((e) => norm(e.textContent) === label).pop();
    if (!hit) { console.log('NOT FOUND: ' + label); return; }
    const target = hit.closest('button,[role=button]') || hit;
    target.dispatchEvent(new PointerEvent('pointerdown', { bubbles: true }));
    target.dispatchEvent(new PointerEvent('pointerup', { bubbles: true }));
    target.click();
  }

  document.addEventListener('DOMContentLoaded', () => {
    const xdc = document.querySelector('x-dc');
    const script = document.querySelector('script[data-dc-script]');
    const props = {};
    try {
      const spec = JSON.parse(script.getAttribute('data-props') || '{}');
      for (const k of Object.keys(spec)) if (spec[k] && 'default' in spec[k]) props[k] = spec[k].default;
    } catch (e) { /* ignore */ }
    if (params.get('theme')) props.startTheme = params.get('theme');
    const Component = new Function('DCLogic', script.textContent + '\n;return Component;')(DCLogic);
    tpl = document.createElement('div');
    for (const child of Array.from(xdc.childNodes)) if (child !== script) tpl.appendChild(child);
    const fast = document.createElement('style');
    fast.textContent = '*,*::before,*::after{animation-duration:0s!important;animation-delay:0s!important;transition-duration:0s!important;transition-delay:0s!important}';
    document.head.appendChild(fast);
    host = document.createElement('div');
    xdc.replaceWith(host);
    comp = new Component(props);
    render();
    if (comp.componentDidMount) comp.componentDidMount();
    const steps = (params.get('steps') || '').split('|').filter(Boolean);
    let delay = 1300;
    for (const step of steps) {
      setTimeout(() => {
        if (step.startsWith('sel:')) {
          const node = host.querySelector(step.slice(4));
          if (node) node.click(); else console.log('NOT FOUND: ' + step);
        } else if (step.startsWith('scroll:')) {
          const amount = Number(step.slice(7));
          host.querySelectorAll('.scroll').forEach((s) => { s.scrollTop = amount; });
        } else clickByText(step);
      }, delay);
      delay += 450;
    }
  });
})();
