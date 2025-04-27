declare type ChangeEvent = Event & {
  target: HTMLInputElement & {
    value?: string;
  };
}
