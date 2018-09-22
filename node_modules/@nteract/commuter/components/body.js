// @flow
import * as React from "react";

import type { ChildrenArray } from "react";

type BodyProps = {
  children?: React.Node
};

const Body = (props: BodyProps) => {
  return props.children;
};

export default Body;
