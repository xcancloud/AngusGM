const getElementTop = (el: HTMLElement) :number => {
  const pele = el.offsetParent;
  if (pele) {
    return getElementTop(pele) + el.offsetTop;
  }
  return el.offsetTop || 200;
};

const getElementLeft = (el: HTMLElement) :number => {
  const pele = el.offsetParent;
  if (pele) {
    return getElementLeft(pele) + el.offsetLeft;
  }
  return el.offsetLeft || 200;
};

export default { getElementTop, getElementLeft };
